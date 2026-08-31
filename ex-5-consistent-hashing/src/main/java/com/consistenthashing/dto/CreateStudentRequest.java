package com.consistenthashing.dto;

public class CreateStudentRequest {
    private String rollNo;
    private String name;
    private String dept;
    private int year;

    public CreateStudentRequest() {}

    public CreateStudentRequest(String rollNo, String name, String dept, int year) {
        this.rollNo = rollNo;
        this.name = name;
        this.dept = dept;
        this.year = year;
    }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}
