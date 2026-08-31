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

            log.info("StudentSeeder: Seeding 20 student records across storage nodes...");
            String[] depts = {"CSE", "ECE", "IT", "EEE", "MECH"};

            for (int i = 1001; i <= 1020; i++) {
                String rollNo = String.valueOf(i);
                String name = "Student_" + i;
                String dept = depts[(i - 1001) % depts.length];
                int year = ((i - 1001) % 4) + 1;

                studentService.createStudent(new CreateStudentRequest(rollNo, name, dept, year));
            }

            log.info("StudentSeeder: Completed seeding 20 records.");
        } catch (Exception e) {
            log.warn("StudentSeeder: Could not seed data (MongoDB containers might still be starting): {}", e.getMessage());
        }
    }
}
