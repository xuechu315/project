-- =====================================================
-- 多银龄守护系统 - 完整数据库建表脚本
-- 数据库版本：MySQL 8.0+
-- 兼容：管理员端 / 医生端 / 老人端 / 家属端
-- =====================================================

CREATE DATABASE IF NOT EXISTS elderly_care CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE elderly_care;

-- =====================================================
-- 1. 用户表（所有角色共用）
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
-- 10. 用药记录表（新增！原 schema 缺失此表）
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
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_family_member_id (family_member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
