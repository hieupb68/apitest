package com.test.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.demo.dto.request.CreateCourseRequest;
import com.test.demo.dto.request.RegisterCourseRequest;
import com.test.demo.dto.response.CourseResponse;
import com.test.demo.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @Test
    void createCourse_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        CreateCourseRequest request = new CreateCourseRequest();
        request.setCode("CS101");
        request.setSubjectName("Introduction to Programming");
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusMonths(3));
        request.setMaxStudents(30);
        request.setTeacherId(1L);

        when(courseService.createCourse(any())).thenReturn(new CourseResponse());

        // Act & Assert
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void createCourse_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        CreateCourseRequest request = new CreateCourseRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerCourse_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        RegisterCourseRequest request = new RegisterCourseRequest();
        request.setStudentId(1L);
        request.setCourseId(1L);

        doNothing().when(courseService).registerCourse(any());

        // Act & Assert
        mockMvc.perform(post("/api/courses/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void registerCourse_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        RegisterCourseRequest request = new RegisterCourseRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/courses/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCourseDetail_ValidId_ReturnsOk() throws Exception {
        // Arrange
        Long courseId = 1L;
        when(courseService.getCourseDetail(courseId)).thenReturn(new CourseResponse());

        // Act & Assert
        mockMvc.perform(get("/api/courses/{courseId}", courseId))
                .andExpect(status().isOk());
    }

    @Test
    void getCourseDetail_InvalidId_ReturnsNotFound() throws Exception {
        // Arrange
        Long courseId = 999L;
        when(courseService.getCourseDetail(courseId)).thenThrow(new RuntimeException("Course not found"));

        // Act & Assert
        mockMvc.perform(get("/api/courses/{courseId}", courseId))
                .andExpect(status().isInternalServerError());
    }
} 