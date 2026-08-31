package com.consistenthashing.storage;

import com.consistenthashing.model.Student;
import org.bson.Document;

public class StudentMapper {

    public static Document toDocument(Student student) {
        if (student == null) return null;
        return new Document("_id", student.getRollNo())
                .append("rollNo", student.getRollNo())
                .append("name", student.getName())
                .append("dept", student.getDept())
                .append("year", student.getYear());
    }

    public static Student toStudent(Document doc) {
        if (doc == null) return null;
        Object rollNoObj = doc.get("rollNo");
        if (rollNoObj == null) rollNoObj = doc.get("RollNo");
        if (rollNoObj == null) rollNoObj = doc.get("_id");
        if (rollNoObj == null) return null;

        String rollNo = String.valueOf(rollNoObj);

        Object nameObj = doc.get("name");
        if (nameObj == null) nameObj = doc.get("Name");
        String name = nameObj != null ? String.valueOf(nameObj) : "Student_" + rollNo;

        Object deptObj = doc.get("dept");
        if (deptObj == null) deptObj = doc.get("Department");
        String dept = deptObj != null ? String.valueOf(deptObj) : "General";

        Integer year = doc.getInteger("year");
        if (year == null && doc.get("Year") instanceof Integer) year = doc.getInteger("Year");
        int y = year != null ? year : 1;

        return new Student(rollNo, name, dept, y);
    }
}
