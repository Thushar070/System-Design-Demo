package com.consistenthashing.service;

import com.consistenthashing.consistenthash.ConsistentHashRing;
import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.dto.CreateStudentRequest;
import com.consistenthashing.dto.StudentDto;
import com.consistenthashing.exception.StudentNotFoundException;
import com.consistenthashing.model.Student;
import com.consistenthashing.storage.StudentStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    private final ConsistentHashRing hashRing;
    private final StudentStore studentStore;

    public StudentService(ConsistentHashRing hashRing, StudentStore studentStore) {
        this.hashRing = hashRing;
        this.studentStore = studentStore;
    }

    public StudentDto createStudent(CreateStudentRequest req) {
        if (req.getRollNo() == null || req.getRollNo().trim().isEmpty()) {
            throw new IllegalArgumentException("rollNo cannot be empty");
        }
        StorageNode targetNode = hashRing.getNode(req.getRollNo());
        if (targetNode == null) {
            throw new IllegalStateException("No storage nodes registered on the hash ring. Add a node first!");
        }

        Student student = new Student(req.getRollNo(), req.getName(), req.getDept(), req.getYear());
        studentStore.save(targetNode, student);
        log.info("Created student rollNo={} -> routed to {}", req.getRollNo(), targetNode.getIdentifier());

        return new StudentDto(student.getRollNo(), student.getName(), student.getDept(), student.getYear(), targetNode.getIdentifier());
    }

    public StudentDto getStudentByRollNo(String rollNo) {
        StorageNode targetNode = hashRing.getNode(rollNo);
        if (targetNode == null) {
            throw new IllegalStateException("No storage nodes registered on the hash ring");
        }

        Optional<Student> studentOpt = studentStore.findByRollNo(targetNode, rollNo);
        if (studentOpt.isEmpty()) {
            // Search all nodes as fallback
            for (StorageNode node : hashRing.getAllNodes()) {
                Optional<Student> s = studentStore.findByRollNo(node, rollNo);
                if (s.isPresent()) {
                    Student student = s.get();
                    return new StudentDto(student.getRollNo(), student.getName(), student.getDept(), student.getYear(), node.getIdentifier());
                }
            }
            throw new StudentNotFoundException("Student with rollNo " + rollNo + " not found");
        }

        Student student = studentOpt.get();
        return new StudentDto(student.getRollNo(), student.getName(), student.getDept(), student.getYear(), targetNode.getIdentifier());
    }

    public void deleteStudent(String rollNo) {
        boolean deleted = false;
        StorageNode targetNode = hashRing.getNode(rollNo);
        if (targetNode != null) {
            deleted = studentStore.deleteByRollNo(targetNode, rollNo);
        }
        if (!deleted) {
            for (StorageNode node : hashRing.getAllNodes()) {
                if (studentStore.deleteByRollNo(node, rollNo)) {
                    deleted = true;
                    break;
                }
            }
        }
        if (!deleted) {
            throw new StudentNotFoundException("Student with rollNo " + rollNo + " not found");
        }
        log.info("Deleted student rollNo={}", rollNo);
    }

    public List<StudentDto> getAllStudents() {
        List<StudentDto> result = new ArrayList<>();
        for (StorageNode node : hashRing.getAllNodes()) {
            List<Student> students = studentStore.findAll(node);
            for (Student s : students) {
                result.add(new StudentDto(s.getRollNo(), s.getName(), s.getDept(), s.getYear(), node.getIdentifier()));
            }
        }
        return result;
    }
}
