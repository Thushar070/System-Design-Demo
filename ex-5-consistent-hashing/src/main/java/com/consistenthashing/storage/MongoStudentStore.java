package com.consistenthashing.storage;

import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.model.Student;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MongoStudentStore implements StudentStore {

    private static final Logger log = LoggerFactory.getLogger(MongoStudentStore.class);
    private static final String DATABASE_NAME = "College";
    private static final String COLLECTION_NAME = "Student";

    private final MongoClientProvider clientProvider;
    private final Map<StorageNode, Map<String, Student>> fallbackStore = new ConcurrentHashMap<>();

    public MongoStudentStore(MongoClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }

    private Map<String, Student> getInMemoryMap(StorageNode node) {
        return fallbackStore.computeIfAbsent(node, n -> new ConcurrentHashMap<>());
    }

    private boolean isMongoAvailable(StorageNode node) {
        try {
            MongoClient client = clientProvider.getClient(node);
            Document ping = new Document("ping", 1);
            client.getDatabase("admin").runCommand(ping);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private MongoCollection<Document> getCollection(StorageNode node) {
        MongoClient client = clientProvider.getClient(node);
        MongoDatabase db = client.getDatabase(DATABASE_NAME);
        return db.getCollection(COLLECTION_NAME);
    }

    @Override
    public void save(StorageNode node, Student student) {
        if (isMongoAvailable(node)) {
            try {
                MongoCollection<Document> collection = getCollection(node);
                Document doc = StudentMapper.toDocument(student);
                collection.replaceOne(new Document("_id", student.getRollNo()), doc, new ReplaceOptions().upsert(true));
                return;
            } catch (Exception e) {
                log.warn("Mongo save failed for node {}, storing in memory fallback: {}", node.getIdentifier(), e.getMessage());
            }
        }
        getInMemoryMap(node).put(student.getRollNo(), student);
    }

    @Override
    public Optional<Student> findByRollNo(StorageNode node, String rollNo) {
        if (isMongoAvailable(node)) {
            try {
                MongoCollection<Document> collection = getCollection(node);
                Document doc = collection.find(new Document("_id", rollNo)).first();
                if (doc != null) {
                    return Optional.ofNullable(StudentMapper.toStudent(doc));
                }
            } catch (Exception e) {
                log.warn("Mongo find failed for node {}: {}", node.getIdentifier(), e.getMessage());
            }
        }
        return Optional.ofNullable(getInMemoryMap(node).get(rollNo));
    }

    @Override
    public boolean deleteByRollNo(StorageNode node, String rollNo) {
        boolean deleted = false;
        if (isMongoAvailable(node)) {
            try {
                MongoCollection<Document> collection = getCollection(node);
                var result = collection.deleteOne(new Document("_id", rollNo));
                deleted = result.getDeletedCount() > 0;
            } catch (Exception e) {
                log.warn("Mongo delete failed for node {}: {}", node.getIdentifier(), e.getMessage());
            }
        }
        if (fallbackStore.containsKey(node)) {
            boolean inMemDeleted = fallbackStore.get(node).remove(rollNo) != null;
            deleted = deleted || inMemDeleted;
        }
        return deleted;
    }

    @Override
    public List<Student> findAll(StorageNode node) {
        Map<String, Student> map = new HashMap<>();
        if (fallbackStore.containsKey(node)) {
            map.putAll(fallbackStore.get(node));
        }
        if (isMongoAvailable(node)) {
            try {
                MongoCollection<Document> collection = getCollection(node);
                for (Document doc : collection.find()) {
                    Student s = StudentMapper.toStudent(doc);
                    if (s != null) {
                        map.put(s.getRollNo(), s);
                    }
                }
            } catch (Exception e) {
                log.warn("Mongo findAll failed for node {}: {}", node.getIdentifier(), e.getMessage());
            }
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public long count(StorageNode node) {
        return findAll(node).size();
    }

    @Override
    public void clear(StorageNode node) {
        if (isMongoAvailable(node)) {
            try {
                getCollection(node).drop();
            } catch (Exception e) {
                log.warn("Mongo drop failed for node {}: {}", node.getIdentifier(), e.getMessage());
            }
        }
        if (fallbackStore.containsKey(node)) {
            fallbackStore.get(node).clear();
        }
    }

    @Override
    public boolean isReachable(StorageNode node) {
        return true;
    }
}
