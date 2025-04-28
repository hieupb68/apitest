
package com.test.demo.service;

import com.test.demo.entity.Teacher;
import com.test.demo.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {
    public final TeacherRepository teacherRepository;
    public Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }
    public List<Teacher> getAllTeacher () {
        return teacherRepository.findAll();
    }
}
