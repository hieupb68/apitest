package com.test.demo.mapper;

import com.test.demo.dto.TeacherDto;
import com.test.demo.entity.Teacher;

public class TeacherMapper {
    public static TeacherDto toDto(Teacher teacher) {
        TeacherDto dto = new TeacherDto();
        dto.setId(teacher.getId());
        dto.setName(teacher.getName());
        return dto;
    }
}
