package com.test.demo.controller;

import com.test.demo.dto.CourseResponse;
import com.test.demo.dto.CreateCourseRequest;
import com.test.demo.dto.RegisterCourseRequest;
import com.test.demo.dto.RegisteredStudentDto;
import com.test.demo.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<Void> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        courseService.createCourse(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterCourseRequest request) {
        courseService.registerCourse(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getDetail(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseDetail(courseId));
    }
}
