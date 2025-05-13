package com.test.demo.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateCourseRequest {
    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code is too long (max 20 characters)")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Course code contains invalid characters")
    private String code;

    @NotBlank(message = "Subject name is required")
    @Size(min = 3, max = 100, message = "Subject name length must be between 3 and 100 characters")
    private String subjectName;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    @NotNull(message = "Max students is required")
    @Min(value = 1, message = "Max students must be positive")
    @Max(value = 100, message = "Max students must not exceed 100")
    private Integer maxStudents;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;
}
