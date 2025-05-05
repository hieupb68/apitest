
package com.test.demo.service;

import com.test.demo.dto.TeacherDto;
import com.test.demo.entity.Teacher;
import com.test.demo.mapper.TeacherMapper;
import com.test.demo.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepo;

    public TeacherDto createTeacher(TeacherDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher name is required");
        }
        Teacher teacher = Teacher.builder()
                .name(dto.getName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return TeacherMapper.toDto(teacherRepo.save(teacher));
    }

    public TeacherDto getTeacher(Long id) {
        Teacher teacher = teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));
        return TeacherMapper.toDto(teacher);
    }

    public List<TeacherDto> getAllTeachers() {
        return teacherRepo.findAll().stream()
                .map(TeacherMapper::toDto)
                .collect(Collectors.toList());
    }
}