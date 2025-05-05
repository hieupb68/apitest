package com.test.demo.service;

import com.test.demo.dto.StudentDto;
import com.test.demo.entity.Student;
import com.test.demo.mapper.StudentMapper;
import com.test.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepo;

    public StudentDto createStudent(StudentDto dto) {
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student code is required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student name is required");
        }
        if (studentRepo.findAll().stream().anyMatch(s -> s.getCode().equals(dto.getCode()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student code already exists");
        }
        Student student = Student.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return StudentMapper.toDto(studentRepo.save(student));
    }

    public StudentDto getStudent(Long id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return StudentMapper.toDto(student);
    }

    public List<StudentDto> getAllStudents() {
        return studentRepo.findAll().stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }
}