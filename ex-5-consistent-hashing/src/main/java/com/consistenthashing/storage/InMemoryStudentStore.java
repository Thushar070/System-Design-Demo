package com.consistenthashing.storage;

import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.model.Student;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStudentStore implements StudentStore {

    private final Map<StorageNode, Map<String, Student>> stores = new ConcurrentHashMap<>();

    private Map<String, Student> getMap(StorageNode node) {
        return stores.computeIfAbsent(node, n -> new ConcurrentHashMap<>());
    }

    @Override
    public void save(StorageNode node, Student student) {
        getMap(node).put(student.getRollNo(), student);
    }

    @Override
    public Optional<Student> findByRollNo(StorageNode node, String rollNo) {
        return Optional.ofNullable(getMap(node).get(rollNo));
    }

    @Override
    public boolean deleteByRollNo(StorageNode node, String rollNo) {
        return getMap(node).remove(rollNo) != null;
    }

    @Override
    public List<Student> findAll(StorageNode node) {
        return new ArrayList<>(getMap(node).values());
    }

    @Override
    public long count(StorageNode node) {
        return getMap(node).size();
    }

    @Override
    public void clear(StorageNode node) {
        getMap(node).clear();
    }

    @Override
    public boolean isReachable(StorageNode node) {
        return true;
    }
}
