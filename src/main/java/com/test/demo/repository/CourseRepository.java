package com.test.demo.repository;

import com.test.demo.entity.Course;
import com.test.demo.entity.Teacher;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacherId(Long teacherId);
    
    List<Course> findByTeacher(Teacher teacher);
    
    boolean existsByName(String name);
    
    long countByTeacher(Teacher teacher);

    @EntityGraph(attributePaths = {"teacher", "students"})
    List<Course> findAll();
}