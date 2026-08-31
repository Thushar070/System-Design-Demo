package com.consistenthashing.controller;

import com.consistenthashing.dto.CreateStudentRequest;
import com.consistenthashing.dto.StudentDto;
import com.consistenthashing.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@RequestBody CreateStudentRequest request) {
        StudentDto dto = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{rollNo}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable String rollNo) {
        StudentDto dto = studentService.getStudentByRollNo(rollNo);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{rollNo}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String rollNo) {
        studentService.deleteStudent(rollNo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }
}
