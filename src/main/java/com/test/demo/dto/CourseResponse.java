package com.test.demo.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseResponse {
    private Long id;
    private String code;
    private String subjectName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxStudents;
    private TeacherDto teacher;
    private List<RegisteredStudentDto> students;
}