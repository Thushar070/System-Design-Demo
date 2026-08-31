package com.consistenthashing.dto;

public class StudentDto {
    private String rollNo;
    private String name;
    private String dept;
    private int year;
    private String node;

    public StudentDto() {}

    public StudentDto(String rollNo, String name, String dept, int year, String node) {
        this.rollNo = rollNo;
        this.name = name;
        this.dept = dept;
        this.year = year;
        this.node = node;
    }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getNode() { return node; }
    public void setNode(String node) { this.node = node; }
}
