package com.test.demo.service;

import com.test.demo.entity.Student;
import com.test.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    public StudentService(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }
    public List<Student> getAllStudent() {
        return studentRepository.findAll();
    }
}