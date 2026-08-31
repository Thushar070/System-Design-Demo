package com.consistenthashing.storage;

import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentStore {
    void save(StorageNode node, Student student);
    Optional<Student> findByRollNo(StorageNode node, String rollNo);
    boolean deleteByRollNo(StorageNode node, String rollNo);
    List<Student> findAll(StorageNode node);
    long count(StorageNode node);
    void clear(StorageNode node);
    boolean isReachable(StorageNode node);
}
