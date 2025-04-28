package com.test.demo.controller;

import com.test.demo.entity.Teacher;
import com.test.demo.service.TeacherService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/teachers")
public class TeacherController {
    private final TeacherService teacherService;
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }
    @PostMapping
    public Teacher createTeacher(@Validated @RequestBody Teacher teacher) {
        return teacherService.createTeacher(teacher);
    }
    @GetMapping
    public List<Teacher> getAllTeacher() {
        return teacherService.getAllTeacher();
    }
}
