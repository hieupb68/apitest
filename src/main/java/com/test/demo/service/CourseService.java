package com.test.demo.service;

import com.test.demo.dto.CourseResponse;
import com.test.demo.dto.CreateCourseRequest;
import com.test.demo.dto.RegisterCourseRequest;
import com.test.demo.dto.RegisteredStudentDto;
import com.test.demo.entity.Course;
import com.test.demo.entity.Student;
import com.test.demo.entity.StudentCourse;
import com.test.demo.entity.Teacher;
import com.test.demo.mapper.CourseMapper;
import com.test.demo.repository.CourseRepository;
import com.test.demo.repository.StudentCourseRepository;
import com.test.demo.repository.StudentRepository;
import com.test.demo.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepo;
    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;
    private final StudentCourseRepository studentCourseRepo;

    public CourseResponse createCourse(CreateCourseRequest req) {
        Teacher teacher = teacherRepo.findById(req.getTeacherId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found")
        );//
        if (teacher.getCourses().size() >= 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher already has 4 courses");
        }
        Course course = Course.builder()
                .code(req.getCode())
                .subjectName(req.getSubjectName())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .maxStudents(req.getMaxStudents())
                .teacher(teacher)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return CourseMapper.toResponse(courseRepo.save(course));
    }

    @Transactional
    public void registerCourse(RegisterCourseRequest req) {
        Student student = studentRepo.findById(req.getStudentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")
        );
        Course course = courseRepo.findById(req.getCourseId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found")
        );

        if (student.getStudentCourses().size() >= 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student already registered 6 courses");
        }
        if (course.getStudentCourses().size() >= course.getMaxStudents()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course is full");
        }
        if (LocalDateTime.now().isAfter(course.getStartTime().minusWeeks(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration is closed");
        }
        boolean alreadyRegistered = student.getStudentCourses().stream()
                .anyMatch(sc -> sc.getCourse().getId().equals(course.getId()));
        if (alreadyRegistered) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student already registered this course");
        }

        StudentCourse sc = StudentCourse.builder()
                .student(student)
                .course(course)
                .registeredAt(LocalDateTime.now())
                .build();
        studentCourseRepo.save(sc);
    }

    public CourseResponse getCourseDetail(Long id) {
        Course course = courseRepo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found")
        );
        return CourseMapper.toResponse(course);
    }
}