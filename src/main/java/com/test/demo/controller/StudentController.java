package com.test.demo.controller;


import com.test.demo.entity.Student;
import com.test.demo.service.StudentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/students")
public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    @PostMapping
    public Student createStudent(@Validated @RequestBody Student student) {
        return studentService.createStudent(student);
    }
    @GetMapping
    public List<Student> getAllStudent() {
        return studentService.getAllStudent();
    }
}
