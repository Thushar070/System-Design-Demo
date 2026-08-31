package com.consistenthashing.model;

import java.util.Objects;

public class Student {

    private String rollNo;
    private String name;
    private String dept;
    private int year;

    public Student() {
    }

    public Student(String rollNo, String name, String dept, int year) {
        this.rollNo = rollNo;
        this.name = name;
        this.dept = dept;
        this.year = year;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return year == student.year &&
                Objects.equals(rollNo, student.rollNo) &&
                Objects.equals(name, student.name) &&
                Objects.equals(dept, student.dept);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rollNo, name, dept, year);
    }

    @Override
    public String toString() {
        return "Student{" +
                "rollNo='" + rollNo + '\'' +
                ", name='" + name + '\'' +
                ", dept='" + dept + '\'' +
                ", year=" + year +
                '}';
    }
}
