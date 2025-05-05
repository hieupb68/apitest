package com.test.demo.dto;

import lombok.Data;

@Data
public class RegisterCourseRequest {
    private Long studentId;
    private Long courseId;
}
