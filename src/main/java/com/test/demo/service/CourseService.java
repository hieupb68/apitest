package com.test.demo.service;

import com.test.demo.dto.response.CourseResponse;
import com.test.demo.dto.request.CreateCourseRequest;
import com.test.demo.dto.request.RegisterCourseRequest;
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
import java.util.ArrayList;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepo;
    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;
    private final StudentCourseRepository studentCourseRepo;

    public CourseResponse createCourse(CreateCourseRequest req) {
        // Check if course code already exists
        if (courseRepo.findAll().stream().anyMatch(c -> c.getCode().equalsIgnoreCase(req.getCode()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course code already exists");
        }

        // Get teacher
        Teacher teacher = teacherRepo.findById(req.getTeacherId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));

        // Check teacher's active courses
        long activeCourses = teacher.getCourses().stream()
                .filter(c -> c.getEndTime().isAfter(LocalDateTime.now()))
                .count();
        if (activeCourses >= 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher already has 4 active courses");
        }

        // Check time conflicts
        boolean hasTimeConflict = teacher.getCourses().stream()
                .anyMatch(c -> c.getEndTime().isAfter(req.getStartTime()) && c.getStartTime().isBefore(req.getEndTime()));
        if (hasTimeConflict) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course time conflicts with another course of the teacher");
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
        // Get student and course
        Student student = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        Course course = courseRepo.findById(req.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        LocalDateTime now = LocalDateTime.now();

        // Check if course has ended
        if (course.getEndTime().isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course has already ended");
        }

        // Check registration deadline
        if (now.isAfter(course.getStartTime().minusWeeks(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration is closed (less than 1 week before start)");
        }

        // Check if already registered
        boolean alreadyRegistered = student.getStudentCourses().stream()
                .anyMatch(sc -> sc.getCourse().getId().equals(course.getId()));
        if (alreadyRegistered) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student already registered this course");
        }

        // Check student's active courses
        long activeRegistered = student.getStudentCourses().stream()
                .filter(sc -> sc.getCourse().getEndTime().isAfter(now))
                .count();
        if (activeRegistered >= 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student already registered 6 active courses");
        }

        // Check if course is full
        if (course.getStudentCourses().size() >= course.getMaxStudents()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course is full");
        }

        // Check time conflicts
        boolean hasTimeConflict = student.getStudentCourses().stream()
                .map(StudentCourse::getCourse)
                .filter(c -> c.getEndTime().isAfter(now))
                .anyMatch(c -> c.getStartTime().isBefore(course.getEndTime()) && c.getEndTime().isAfter(course.getStartTime()));
        if (hasTimeConflict) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course time conflicts with another registered course");
        }

        StudentCourse sc = StudentCourse.builder()
                .student(student)
                .course(course)
                .registeredAt(now)
                .build();
        studentCourseRepo.save(sc);
    }

    public CourseResponse getCourseDetail(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        CourseResponse response = CourseMapper.toResponse(course);

        if (response.getStudents() == null) {
            response.setStudents(new ArrayList<>());
        } else {
            response.getStudents().sort(Comparator.comparing(s -> s.getStudent().getName()));
        }

        return response;
    }
}
