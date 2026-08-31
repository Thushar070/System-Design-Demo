package com.consistenthashing.storage;

import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.model.Student;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MongoStudentStore implements StudentStore {

    private static final String DATABASE_NAME = "College";
    private static final String COLLECTION_NAME = "Student";

    private final MongoClientProvider clientProvider;

    public MongoStudentStore(MongoClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }

    private MongoCollection<Document> getCollection(StorageNode node) {
        MongoClient client = clientProvider.getClient(node);
        MongoDatabase db = client.getDatabase(DATABASE_NAME);
        return db.getCollection(COLLECTION_NAME);
    }

    @Override
    public void save(StorageNode node, Student student) {
        MongoCollection<Document> collection = getCollection(node);
        Document doc = StudentMapper.toDocument(student);
        collection.replaceOne(new Document("_id", student.getRollNo()), doc, new ReplaceOptions().upsert(true));
    }

    @Override
    public Optional<Student> findByRollNo(StorageNode node, String rollNo) {
        MongoCollection<Document> collection = getCollection(node);
        Document doc = collection.find(new Document("_id", rollNo)).first();
        return Optional.ofNullable(StudentMapper.toStudent(doc));
    }

    @Override
    public boolean deleteByRollNo(StorageNode node, String rollNo) {
        MongoCollection<Document> collection = getCollection(node);
        var result = collection.deleteOne(new Document("_id", rollNo));
        return result.getDeletedCount() > 0;
    }

    @Override
    public List<Student> findAll(StorageNode node) {
        MongoCollection<Document> collection = getCollection(node);
        List<Student> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            Student s = StudentMapper.toStudent(doc);
            if (s != null) {
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public long count(StorageNode node) {
        return getCollection(node).countDocuments();
    }

    @Override
    public void clear(StorageNode node) {
        getCollection(node).drop();
    }

    @Override
    public boolean isReachable(StorageNode node) {
        try {
            MongoClient client = clientProvider.getClient(node);
            Document ping = new Document("ping", 1);
            client.getDatabase("admin").runCommand(ping);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
