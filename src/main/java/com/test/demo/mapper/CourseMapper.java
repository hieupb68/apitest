package com.test.demo.mapper;

import com.test.demo.dto.CourseResponse;
import com.test.demo.entity.Course;

import java.util.Comparator;
import java.util.stream.Collectors;

public class CourseMapper {
    public static CourseResponse toResponse(Course course) {
        CourseResponse res = new CourseResponse();
        res.setId(course.getId());
        res.setCode(course.getCode());
        res.setSubjectName(course.getSubjectName());
        res.setStartTime(course.getStartTime());
        res.setEndTime(course.getEndTime());
        res.setMaxStudents(course.getMaxStudents());
        res.setTeacher(TeacherMapper.toDto(course.getTeacher()));
        if (course.getStudentCourses() != null) {
            res.setStudents(course.getStudentCourses().stream()
                    .map(StudentCourseMapper::toRegisteredStudentDto)
                    .sorted(Comparator.comparing(o -> o.getStudent().getName()))
                    .collect(Collectors.toList()));
        }
        return res;
    }
}
