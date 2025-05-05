package com.test.demo.mapper;

import com.test.demo.dto.StudentDto;
import com.test.demo.entity.Student;

public class StudentMapper {
    public static StudentDto toDto(Student student) {
        StudentDto dto = new StudentDto();
        dto.setId(student.getId());
        dto.setCode(student.getCode());
        dto.setName(student.getName());
        return dto;
    }
}
