package com.test.demo.mapper;

import com.test.demo.dto.RegisteredStudentDto;
import com.test.demo.entity.StudentCourse;

public class StudentCourseMapper {
    public static RegisteredStudentDto toRegisteredStudentDto(StudentCourse sc) {
        RegisteredStudentDto dto = new RegisteredStudentDto();
        dto.setStudent(StudentMapper.toDto(sc.getStudent()));
        dto.setRegisteredAt(sc.getRegisteredAt());
        return dto;
    }
}