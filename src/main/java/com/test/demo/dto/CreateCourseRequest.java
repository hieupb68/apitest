package com.test.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateCourseRequest {
    private String code;
    private String subjectName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxStudents;
    private Long teacherId;
}
