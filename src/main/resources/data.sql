-- Tạo bảng teachers nếu chưa tồn tại
CREATE TABLE IF NOT EXISTS teachers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME
);

-- Tạo bảng students nếu chưa tồn tại
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    student_number VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME
);

-- Tạo bảng course nếu chưa tồn tại
CREATE TABLE IF NOT EXISTS course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    subject VARCHAR(255),
    start_time DATETIME,
    end_time DATETIME,
    created_at DATETIME,
    updated_at DATETIME,
    teacher_id BIGINT,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
);

-- Tạo bảng course_student nếu chưa tồn tại
CREATE TABLE IF NOT EXISTS course_student (
    courses_id BIGINT,
    student_id BIGINT,
    PRIMARY KEY (course_id, student_id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (student_id) REFERENCES students(id)
);

-- -- Thêm dữ liệu cho bảng teachers
-- INSERT INTO teachers (name, created_at, updated_at) VALUES
-- ('Nguyen Van A', NOW(), NOW()),
-- ('Tran Thi B', NOW(), NOW()),
-- ('Le Van C', NOW(), NOW());
-- --
-- -- -- Thêm dữ liệu cho bảng students
-- INSERT INTO students (name, code, created_at, updated_at) VALUES
-- ('Pham Van D', 'SV001', NOW(), NOW()),
-- ('Hoang Thi E', 'SV002', NOW(), NOW()),
-- ('Vu Van F', 'SV003', NOW(), NOW()),
-- ('Nguyen Thi G', 'SV004', NOW(), NOW()),
-- ('Tran Van H', 'SV005', NOW(), NOW());
--
-- -- Thêm dữ liệu cho bảng course
-- INSERT INTO course (name, subject, start_time, end_time, created_at, updated_at, teacher_id) VALUES
-- ('Java Basic', 'Programming', '2024-05-01 08:00:00', '2024-05-01 10:00:00', NOW(), NOW(), 1),
-- ('Spring Boot', 'Web Development', '2024-05-02 13:00:00', '2024-05-02 15:00:00', NOW(), NOW(), 2),
-- ('Database Design', 'Database', '2024-05-03 09:00:00', '2024-05-03 11:00:00', NOW(), NOW(), 3);
--
-- -- Thêm dữ liệu cho bảng course_student
-- INSERT INTO course_student (course_id, student_id) VALUES
-- (1, 1), -- Java Basic - Pham Van D
-- (1, 2), -- Java Basic - Hoang Thi E
-- (2, 2), -- Spring Boot - Hoang Thi E
-- (2, 3), -- Spring Boot - Vu Van F
-- (3, 1), -- Database Design - Pham Van D
-- (3, 4), -- Database Design - Nguyen Thi G
-- (3, 5); -- Database Design - Tran Van H