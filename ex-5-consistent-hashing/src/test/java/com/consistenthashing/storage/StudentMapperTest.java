package com.consistenthashing.storage;

import com.consistenthashing.model.Student;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentMapperTest {

    @Test
    void testToDocument() {
        Student student = new Student("1001", "Charlie", "IT", 4);
        Document doc = StudentMapper.toDocument(student);

        assertNotNull(doc);
        assertEquals("1001", doc.getString("_id"));
        assertEquals("1001", doc.getString("rollNo"));
        assertEquals("Charlie", doc.getString("name"));
        assertEquals("IT", doc.getString("dept"));
        assertEquals(4, doc.getInteger("year"));
    }

    @Test
    void testToStudent() {
        Document doc = new Document("_id", "1002")
                .append("rollNo", "1002")
                .append("name", "David")
                .append("dept", "MECH")
                .append("year", 1);

        Student student = StudentMapper.toStudent(doc);
        assertNotNull(student);
        assertEquals("1002", student.getRollNo());
        assertEquals("David", student.getName());
        assertEquals("MECH", student.getDept());
        assertEquals(1, student.getYear());
    }
}
