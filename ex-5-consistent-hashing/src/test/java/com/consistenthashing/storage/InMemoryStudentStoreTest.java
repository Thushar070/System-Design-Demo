package com.consistenthashing.storage;

import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStudentStoreTest {

    private InMemoryStudentStore store;
    private StorageNode node;

    @BeforeEach
    void setUp() {
        store = new InMemoryStudentStore();
        node = new StorageNode("localhost", 27017);
    }

    @Test
    void testSaveAndFind() {
        Student student = new Student("1001", "Alice", "CSE", 3);
        store.save(node, student);

        Optional<Student> found = store.findByRollNo(node, "1001");
        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getName());
        assertEquals(1, store.count(node));
    }

    @Test
    void testDelete() {
        Student student = new Student("1002", "Bob", "ECE", 2);
        store.save(node, student);
        assertTrue(store.deleteByRollNo(node, "1002"));
        assertFalse(store.findByRollNo(node, "1002").isPresent());
        assertEquals(0, store.count(node));
    }

    @Test
    void testClear() {
        store.save(node, new Student("1001", "Alice", "CSE", 3));
        store.save(node, new Student("1002", "Bob", "ECE", 2));
        assertEquals(2, store.count(node));
        store.clear(node);
        assertEquals(0, store.count(node));
    }
}
