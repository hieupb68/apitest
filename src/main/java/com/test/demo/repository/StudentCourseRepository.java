package com.test.demo.repository;

import com.test.demo.entity.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    long countByStudentId(Long studentId);
}
