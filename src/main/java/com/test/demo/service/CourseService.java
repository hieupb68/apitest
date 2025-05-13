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
        // Validate: code
        if (req.getCode() == null || req.getCode().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course code is required");
        if (req.getCode().length() > 20)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course code is too long (max 20 characters)");
        if (!req.getCode().matches("^[A-Za-z0-9_-]+$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course code contains invalid characters");
        if (courseRepo.findAll().stream().anyMatch(c -> c.getCode().equalsIgnoreCase(req.getCode())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course code already exists");

        // Validate: subjectName
        if (req.getSubjectName() == null || req.getSubjectName().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subject name is required");
        if (req.getSubjectName().length() < 3 || req.getSubjectName().length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subject name length must be between 3 and 100 characters");

        // Validate: startTime & endTime
        if (req.getStartTime() == null || req.getEndTime() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start and end time are required");
        if (req.getStartTime().isAfter(req.getEndTime()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
        if (req.getStartTime().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be in the future");
        if (req.getEndTime().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be in the future");

        // Validate: maxStudents
        if (req.getMaxStudents() == null || req.getMaxStudents() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max students must be positive");
        if (req.getMaxStudents() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max students must not exceed 100");

        // Validate: teacherId
        Teacher teacher = teacherRepo.findById(req.getTeacherId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));

        // Giảng viên chỉ dạy tối đa 4 lớp đang hoạt động
        long activeCourses = teacher.getCourses().stream()
                .filter(c -> c.getEndTime().isAfter(LocalDateTime.now()))
                .count();
        if (activeCourses >= 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher already has 4 active courses");
        }

        // Không cho phép trùng giờ với lớp khác của giảng viên
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
        // Validate: studentId và courseId phải tồn tại
        Student student = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        Course course = courseRepo.findById(req.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        LocalDateTime now = LocalDateTime.now();

        // Lớp chưa kết thúc
        if (course.getEndTime().isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course has already ended");
        }

        // Chỉ cho đăng ký trước khi bắt đầu 1 tuần
        if (now.isAfter(course.getStartTime().minusWeeks(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration is closed (less than 1 week before start)");
        }

        // Sinh viên chưa đăng ký lớp này
        boolean alreadyRegistered = student.getStudentCourses().stream()
                .anyMatch(sc -> sc.getCourse().getId().equals(course.getId()));
        if (alreadyRegistered) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student already registered this course");
        }

        // Sinh viên chỉ được đăng ký tối đa 6 lớp đang hoạt động
        long activeRegistered = student.getStudentCourses().stream()
                .filter(sc -> sc.getCourse().getEndTime().isAfter(now))
                .count();
        if (activeRegistered >= 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student already registered 6 active courses");
        }

        // Lớp chưa đủ học viên
        if (course.getStudentCourses().size() >= course.getMaxStudents()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course is full");
        }

        // Không cho phép sinh viên đăng ký 2 lớp trùng giờ
        boolean hasTimeConflict = student.getStudentCourses().stream()
                .map(StudentCourse::getCourse)
                .filter(c -> c.getEndTime().isAfter(now)) // chỉ xét các lớp còn hoạt động
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

        // Đảm bảo danh sách sinh viên luôn là list rỗng nếu không có ai đăng ký
        if (response.getStudents() == null) {
            response.setStudents(new ArrayList<>());
        } else {
            // Sắp xếp danh sách sinh viên theo tên ABC
            response.getStudents().sort(Comparator.comparing(s -> s.getStudent().getName()));
        }

        return response;
    }
}
