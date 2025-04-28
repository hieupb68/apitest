package com.test.demo.service;

import com.test.demo.entity.Course;
import com.test.demo.entity.Student;
import com.test.demo.entity.Teacher;
import com.test.demo.repository.CourseRepository;
import com.test.demo.repository.StudentRepository;
import com.test.demo.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    public Course createCourse(Course course) {
        // Validate: môn học không trùng tên
        if (courseRepository.existsByName(course.getName())) {
            throw new RuntimeException("Course name '" + course.getName() + "' already exists");
        }

        // Validate: giáo viên không dạy quá 4 lớp
        Teacher teacher = course.getTeacher();
        if (teacher != null) {
            long teacherCourseCount = courseRepository.countByTeacher(teacher);
            if (teacherCourseCount >= 4) {
                throw new RuntimeException("Teacher '" + teacher.getName() + "' can only teach maximum 4 courses");
            }
        }

        return courseRepository.save(course);
    }

    public List<Course> getAllCourse() {
        return courseRepository.findAll().stream()
                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                .collect(Collectors.toList());
    }

    public Course addStudentToCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course with id " + courseId + " not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student with id " + studentId + " not found"));

        // Validate: học sinh không đăng ký quá 6 lớp
        if (student.getCourses().size() >= 6) {
            throw new RuntimeException("Student '" + student.getName() + "' can only register maximum 6 courses");
        }

        // Validate: chỉ cho phép đăng ký trước thời gian bắt đầu học 1 tuần
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekBeforeStart = course.getStartTime().minusWeeks(1);
        if (now.isAfter(oneWeekBeforeStart)) {
            throw new RuntimeException("Can only register for a course at least 1 week before it starts. Course starts at: " + course.getStartTime());
        }

        course.getStudents().add(student);
        return courseRepository.save(course);
    }

    public List<Course> findCourseByTeacher(Long id) {
        return courseRepository.findByTeacherId(id).stream()
                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                .collect(Collectors.toList());
    }

    public Optional<Course> getCourseById(Long id) {
        //check validate null
        return courseRepository.findById(id);
    }

    public List<Student> getStudentsInCourseSortedByName(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        return course.getStudents().stream()
                .sorted((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()))
                .collect(Collectors.toList());
    }

    private boolean isSameWeek(LocalDateTime t1, LocalDateTime t2) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int week1 = t1.get(weekFields.weekOfWeekBasedYear());
        int week2 = t2.get(weekFields.weekOfWeekBasedYear());
        int year1 = t1.getYear();
        int year2 = t2.getYear();
        return week1 == week2 && year1 == year2;
    }

    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
