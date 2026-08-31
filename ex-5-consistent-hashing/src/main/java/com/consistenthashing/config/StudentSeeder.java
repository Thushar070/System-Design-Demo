package com.consistenthashing.config;

import com.consistenthashing.dto.CreateStudentRequest;
import com.consistenthashing.service.DistributionService;
import com.consistenthashing.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StudentSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StudentSeeder.class);

    private final StudentService studentService;
    private final DistributionService distributionService;

    public StudentSeeder(StudentService studentService, DistributionService distributionService) {
        this.studentService = studentService;
        this.distributionService = distributionService;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            long existingCount = distributionService.getDistributionReport().getTotal();
            if (existingCount > 0) {
                log.info("StudentSeeder: {} existing record(s) found; skipping seed", existingCount);
                return;
            }

            log.info("StudentSeeder: Adding 3 initial sample student records...");
            studentService.createStudent(new CreateStudentRequest("101", "Alice", "CSE", 3));
            studentService.createStudent(new CreateStudentRequest("102", "Bob", "ECE", 2));
            studentService.createStudent(new CreateStudentRequest("103", "Charlie", "IT", 4));

            log.info("StudentSeeder: Sample seeding complete.");
        } catch (Exception e) {
            log.warn("StudentSeeder: Skipping seeding: {}", e.getMessage());
        }
    }
}
