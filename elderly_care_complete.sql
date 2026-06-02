.................................

-- =====================================================
-- 多银龄守护系统 - 完整数据库建表+测试数据脚本
-- 数据库版本：MySQL 8.0+
-- 数据库名：elderly_care
-- 兼容端：管理员端 / 医生端 / 老人端 / 家属端
-- 所有测试账号密码均为：123456
-- =====================================================

-- =====================================================
-- 第一部分：建表
-- =====================================================
CREATE DATABASE IF NOT EXISTS elderly_care CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE elderly_care;

-- =====================================================
-- 1. 用户表（所有角色共用）
-- user_type: elder(老人) / family(家属) / doctor(医生) / admin(管理员)
-- =====================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    user_type VARCHAR(20) NOT NULL COMMENT 'elder/family/doctor/admin',
    name VARCHAR(20) NOT NULL,
    age INT,
    gender VARCHAR(10),
    blood_type VARCHAR(10),
    height FLOAT,
    weight FLOAT,
    phone VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. 医生表
-- =====================================================
DROP TABLE IF EXISTS doctor;
CREATE TABLE doctor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(20) NOT NULL,
    phone VARCHAR(20),
    department VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. 老人表
-- =====================================================
DROP TABLE IF EXISTS elder;
CREATE TABLE elder (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    age INT,
    gender VARCHAR(10),
    blood_type VARCHAR(10),
    height FLOAT,
    weight FLOAT,
    emergency_contact_id INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 4. 家属表（管理员端：老人与家属的绑定关系）
-- =====================================================
DROP TABLE IF EXISTS family_member;
CREATE TABLE family_member (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    elder_id INT,
    name VARCHAR(20) NOT NULL,
    relationship VARCHAR(20),
    phone VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_elder_id (elder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 5. 老人-医生关联表
-- =====================================================
DROP TABLE IF EXISTS elder_doctor_relation;
CREATE TABLE elder_doctor_relation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    elder_id INT NOT NULL,
    doctor_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_elder_doctor (elder_id, doctor_id),
    INDEX idx_elder_id (elder_id),
    INDEX idx_doctor_id (doctor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 6. 操作日志表
-- =====================================================
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator VARCHAR(50) NOT NULL,
    operation VARCHAR(500) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 7. 健康数据表
-- =====================================================
DROP TABLE IF EXISTS health_data;
CREATE TABLE health_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    heart_rate INT,
    systolic_pressure INT,
    diastolic_pressure INT,
    steps INT DEFAULT 0,
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_recorded_at (recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 8. 异常事件表
--  type: 跌倒 / 心率异常 / 血压异常 / 长时间不动
--  severity: 紧急 / 警告 / 注意 / 正常
-- =====================================================
DROP TABLE IF EXISTS abnormal_event;
CREATE TABLE abnormal_event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL COMMENT '跌倒/心率异常/血压异常/长时间不动',
    severity VARCHAR(20) NOT NULL COMMENT '紧急/警告/注意/正常',
    confidence FLOAT,
    detected_by VARCHAR(30),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    resolved INT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_severity (severity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 9. 药品表
-- =====================================================
DROP TABLE IF EXISTS medication;
CREATE TABLE medication (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    dosage VARCHAR(50),
    frequency VARCHAR(50),
    time VARCHAR(50) COMMENT '提醒时间，如 08:00',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 10. 用药记录表
--  status: 已服用 / 漏服 / 跳过
-- =====================================================
DROP TABLE IF EXISTS medication_record;
CREATE TABLE medication_record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medication_id INT NOT NULL,
    taken_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL COMMENT '已服用/漏服/跳过',
    INDEX idx_medication_id (medication_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 11. 咨询记录表
--  type: 文本 / 语音 / 图片
-- =====================================================
DROP TABLE IF EXISTS consultation;
CREATE TABLE consultation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    response TEXT,
    type VARCHAR(20) DEFAULT '文本' COMMENT '文本/语音/图片',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 12. SOS求助记录表
--  status: PENDING / 已响应 / 已闭环
-- =====================================================
DROP TABLE IF EXISTS sos_record;
CREATE TABLE sos_record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    location VARCHAR(200),
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/已响应/已闭环',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 13. 家属绑定表（移动端：家属与老人的绑定关系）
-- =====================================================
DROP TABLE IF EXISTS family_bind;
CREATE TABLE family_bind (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT COMMENT '老人用户ID',
    family_id INT NOT NULL COMMENT '家属用户ID',
    relationship VARCHAR(20),
    name VARCHAR(20),
    phone VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_elder_family (user_id, family_id),
    INDEX idx_elder_id (user_id),
    INDEX idx_family_id (family_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 14. 联系记录表
-- =====================================================
DROP TABLE IF EXISTS contact_record;
CREATE TABLE contact_record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    family_member_id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'sent',
    message TEXT COMMENT '通知消息内容',
    target_name VARCHAR(50) COMMENT '目标姓名（冗余存储）',
    target_phone VARCHAR(20) COMMENT '目标电话（冗余存储）',
    acknowledged_at DATETIME COMMENT '通知确认时间，null=未确认',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_family_member_id (family_member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 第二部分：测试数据
-- 所有账号密码均为 123456
-- =====================================================

-- -------------------- 用户数据 --------------------
-- 医生 + 管理员
INSERT INTO `user` (username, password, user_type, name, age, gender, phone) VALUES
('doctor1', '123456', 'doctor', '王建国', 45, '男', '13800138001'),
('doctor2', '123456', 'doctor', '李医生', 38, '女', '13800138002'),
('admin', '123456', 'admin', '系统管理员', 35, '男', '13800138000');

-- 老人
INSERT INTO `user` (username, password, user_type, name, age, gender, blood_type, height, weight, phone) VALUES
('elderly1', '123456', 'elder', '张大爷', 75, '男', 'O+', 172, 68, '13900000001'),
('elderly2', '123456', 'elder', '李芳', 68, '女', 'A+', 158, 55, '13900000002'),
('elderly3', '123456', 'elder', '王林', 72, '男', 'B+', 175, 72, '13900000003'),
('elderly4', '123456', 'elder', '陈秀莲', 65, '女', 'AB+', 155, 52, '13900000004'),
('elderly5', '123456', 'elder', '赵德明', 78, '男', 'O-', 168, 65, '13900000005'),
('elderly6', '123456', 'elder', '孙桂英', 71, '女', 'A-', 160, 58, '13900000006');

-- 家属
INSERT INTO `user` (username, password, user_type, name, age, gender, phone) VALUES
('family1', '123456', 'family', '张小明', 42, '男', '13700000001'),
('family2', '123456', 'family', '李小红', 38, '女', '13700000002'),
('family3', '123456', 'family', '王小强', 35, '男', '13700000003');

-- -------------------- 医生辅助表 --------------------
INSERT INTO doctor (user_id, name, phone, department) VALUES
(1, '王建国', '13800138001', '全科'),
(2, '李医生', '13800138002', '内科');

-- -------------------- 老人辅助表 --------------------
INSERT INTO elder (user_id, age, gender, blood_type, height, weight) VALUES
(4, 75, '男', 'O+', 172, 68),
(5, 68, '女', 'A+', 158, 55),
(6, 72, '男', 'B+', 175, 72),
(7, 65, '女', 'AB+', 155, 52),
(8, 78, '男', 'O-', 168, 65),
(9, 71, '女', 'A-', 160, 58);

-- -------------------- 家属辅助表（管理端） --------------------
INSERT INTO family_member (user_id, elder_id, name, relationship, phone) VALUES
(10, 1, '张小明', '儿子', '13700000001'),
(11, 2, '李小红', '女儿', '13700000002'),
(12, 3, '王小强', '孙子', '13700000003');

-- --- -------------------- 老人-医生关联 --------------------
-- 王建国医生(doctor_id=1)管理：张大爷、李芳、王林
-- 李医生(doctor_id=2)管理：陈秀莲、赵德明、孙桂英
INSERT INTO elder_doctor_relation (elder_id, doctor_id) VALUES
(1, 1), (2, 1), (3, 1),
(4, 2), (5, 2), (6, 2);

-- -------------------- 健康数据 --------------------
INSERT INTO health_data (user_id, heart_rate, systolic_pressure, diastolic_pressure, steps) VALUES
(4, 118, 185, 110, 1250),   -- 张大爷
(5, 92, 142, 88, 3102),    -- 李芳
(6, 78, 125, 82, 4521),    -- 王林
(7, 72, 118, 78, 5890),    -- 陈秀莲
(8, 68, 130, 85, 2340),    -- 赵德明
(9, 85, 135, 88, 4156);    -- 孙桂英

-- 张大爷的详细时序数据（供趋势图展示）
INSERT INTO health_data (user_id, heart_rate, systolic_pressure, diastolic_pressure, steps, recorded_at) VALUES
(4, 75, 120, 80, 4520, '2026-04-27 08:00:00'),
(4, 78, 125, 82, 5100, '2026-04-27 10:00:00'),
(4, 82, 130, 85, 3800, '2026-04-27 12:00:00'),
(4, 76, 122, 81, 4200, '2026-04-27 14:00:00'),
(4, 80, 128, 84, 3500, '2026-04-27 16:00:00'),
(4, 85, 135, 88, 2800, '2026-04-27 18:00:00'),
(4, 72, 118, 78, 1200, '2026-04-27 20:00:00'),
(4, 68, 115, 75, 500, '2026-04-27 22:00:00'),
(4, 70, 118, 78, 3200, '2026-04-28 06:00:00'),
(4, 75, 122, 80, 4100, '2026-04-28 08:00:00'),
(4, 88, 145, 92, 2800, '2026-04-28 10:00:00'),
(4, 118, 185, 110, 1250, '2026-04-28 10:24:00');

-- -------------------- 异常事件 --------------------
INSERT INTO abnormal_event (user_id, type, severity, confidence, detected_by) VALUES
(4, '跌倒', '紧急', 0.968, '行为分析Agent'),
(4, '血压异常', '紧急', 0.985, '健康监测Agent'),
(5, '血压异常', '警告', 0.892, '健康监测Agent'),
(9, '心率异常', '注意', 0.856, '健康监测Agent'),
(6, '心率异常', '正常', 0.723, '健康监测Agent');

-- -------------------- 药品表 --------------------
INSERT INTO medication (user_id, name, description, dosage, frequency, time) VALUES
(4, '硝苯地平缓释片', '降压药', '2片', '每日1次', '08:00'),
(4, '阿司匹林肠溶片', '抗血小板聚集', '1片', '每日1次', '09:00'),
(5, '硝苯地平缓释片', '降压药', '1片', '每日1次', '08:00'),
(9, '二甲双胍', '降糖药', '1片', '每日2次', '08:00,20:00'),
(4, '丹参滴丸', '活血化瘀', '10丸', '每日3次', '08:00,12:00,18:00');

-- -------------------- 用药记录 --------------------
INSERT INTO medication_record (medication_id, taken_at, status) VALUES
(1, '2026-04-28 08:00:00', '已服用'),
(2, '2026-04-28 09:00:00', '已服用'),
(3, '2026-04-28 08:00:00', '已服用'),
(4, '2026-04-28 08:00:00', '已服用'),
(4, '2026-04-28 20:00:00', '漏服'),
(5, '2026-04-28 08:00:00', '已服用');

-- -------------------- 家属绑定关系（移动端） --------------------
INSERT INTO family_bind (user_id, family_id, relationship, name, phone) VALUES
(4, 10, '儿子', '张小明', '13700000001'),   -- 张大爷 ← 张小明
(5, 11, '女儿', '李小红', '13700000002'),  -- 李芳 ← 李小红
(6, 12, '孙子', '王小强', '13700000003'); -- 王林 ← 王小强

-- -------------------- 操作日志 --------------------
INSERT INTO operation_log (operator, operation) VALUES
('系统管理员', '登录系统'),
('系统管理员', '创建用户: elderly1');
