package com.consistenthashing.service;

import com.consistenthashing.consistenthash.ConsistentHashRing;
import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.dto.CreateStudentRequest;
import com.consistenthashing.dto.StudentDto;
import com.consistenthashing.exception.StudentNotFoundException;
import com.consistenthashing.storage.InMemoryStudentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    private StudentService service;
    private ConsistentHashRing ring;
    private InMemoryStudentStore store;

    @BeforeEach
    void setUp() {
        StorageNode n1 = new StorageNode("localhost", 27017);
        StorageNode n2 = new StorageNode("localhost", 27018);
        StorageNode n3 = new StorageNode("localhost", 27019);
        ring = new ConsistentHashRing(150, List.of(n1, n2, n3));
        store = new InMemoryStudentStore();
        service = new StudentService(ring, store);
    }

    @Test
    void testCreateAndGetStudent() {
        CreateStudentRequest req = new CreateStudentRequest("1001", "Eve", "EEE", 2);
        StudentDto dto = service.createStudent(req);

        assertNotNull(dto);
        assertEquals("1001", dto.getRollNo());

        StudentDto fetched = service.getStudentByRollNo("1001");
        assertEquals("Eve", fetched.getName());
    }

    @Test
    void testDeleteStudent() {
        service.createStudent(new CreateStudentRequest("1002", "Frank", "CSE", 3));
        service.deleteStudent("1002");

        assertThrows(StudentNotFoundException.class, () -> service.getStudentByRollNo("1002"));
    }

    @Test
    void testGetAllStudents() {
        service.createStudent(new CreateStudentRequest("1001", "A", "CSE", 1));
        service.createStudent(new CreateStudentRequest("1002", "B", "ECE", 2));
        service.createStudent(new CreateStudentRequest("1003", "C", "IT", 3));

        List<StudentDto> all = service.getAllStudents();
        assertEquals(3, all.size());
    }
}
