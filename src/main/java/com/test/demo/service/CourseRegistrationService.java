package com.test.demo.service;

import com.test.demo.dto.request.RegisterCourseRequest;
import com.test.demo.entity.Course;
import com.test.demo.entity.Student;
import com.test.demo.entity.StudentCourse;
import com.test.demo.repository.CourseRepository;
import com.test.demo.repository.StudentCourseRepository;
import com.test.demo.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CourseRegistrationService {
    private final CourseRepository courseRepo;
    private final StudentRepository studentRepo;
    private final StudentCourseRepository studentCourseRepo;

    @Transactional
    public void registerStudent(RegisterCourseRequest req) {
        Course course = courseRepo.findById(req.getCourseId()).orElseThrow();
        Student student = studentRepo.findById(req.getStudentId()).orElseThrow();

        // Kiểm tra đã đăng ký chưa
        if (studentCourseRepo.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new RuntimeException("Student already registered this course");
        }
        // Kiểm tra lớp đã đủ học viên chưa
        if (course.getStudentCourses().size() >= course.getMaxStudents()) {
            throw new RuntimeException("Course is full");
        }
        // Kiểm tra sinh viên đã đăng ký đủ 6 lớp chưa
        if (studentCourseRepo.countByStudentId(student.getId()) >= 6) {
            throw new RuntimeException("Student already registered 6 courses");
        }
        // Kiểm tra thời gian đăng ký
        if (LocalDateTime.now().isAfter(course.getStartTime().minusWeeks(1))) {
            throw new RuntimeException("Cannot register less than 1 week before course start");
        }

        StudentCourse reg = StudentCourse.builder()
                .course(course)
                .student(student)
                .registeredAt(LocalDateTime.now())
                .build();
        studentCourseRepo.save(reg);
    }
}