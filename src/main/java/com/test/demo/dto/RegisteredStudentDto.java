package com.test.demo.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RegisteredStudentDto {
    private StudentDto student;
    private LocalDateTime registeredAt;
}
