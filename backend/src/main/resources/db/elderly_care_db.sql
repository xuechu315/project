-- =====================================================
-- 多银龄守护系统 数据库建表语句（修正版）
-- 数据库版本：MySQL 8.0+
-- =====================================================

-- 创建数据库（如需要）
-- CREATE DATABASE IF NOT EXISTS elderly_guard DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE elderly_guard;

-- =====================================================
-- 第一阶段：创建不依赖外键的基础表
-- =====================================================

-- 1. 用户表
CREATE TABLE `user` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '登录账号',
    `password` VARCHAR(100) NOT NULL COMMENT '加密密码',
    `user_type` ENUM('elder', 'family', 'doctor', 'admin') NOT NULL COMMENT '用户类型',
    `name` VARCHAR(20) NOT NULL COMMENT '姓名',
    `phone` VARCHAR(20) NULL COMMENT '联系电话',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 医生表
CREATE TABLE `doctor` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '医生ID',
    `user_id` INT NULL COMMENT '关联的用户ID',
    `name` VARCHAR(20) NOT NULL COMMENT '姓名',
    `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `department` VARCHAR(50) NULL COMMENT '所属科室',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生表';

-- =====================================================
-- 第二阶段：创建依赖 user 表但可能被其他表引用的表
-- =====================================================

-- 3. 家属成员表（先创建，因为 elder 表会引用它）
CREATE TABLE `family_member` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '家属ID',
    `user_id` INT NULL COMMENT '关联的用户ID',
    `elder_id` INT NULL COMMENT '关联的老人ID',
    `name` VARCHAR(20) NOT NULL COMMENT '家属姓名',
    `relationship` VARCHAR(20) NULL COMMENT '关系（子女/配偶等）',
    `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_elder_id` (`elder_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家属成员表';

-- 4. 老人表（引用 user 和 family_member）
CREATE TABLE `elder` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '老人ID',
    `user_id` INT NOT NULL COMMENT '关联的用户ID（老人角色）',
    `age` INT NULL COMMENT '年龄',
    `gender` VARCHAR(10) NULL COMMENT '性别',
    `blood_type` VARCHAR(10) NULL COMMENT '血型',
    `height` FLOAT NULL COMMENT '身高（cm）',
    `weight` FLOAT NULL COMMENT '体重（kg）',
    `emergency_contact_id` INT NULL COMMENT '紧急联系人ID（关联家属表）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_emergency_contact_id` (`emergency_contact_id`),
    CONSTRAINT `fk_elder_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_elder_emergency_contact` FOREIGN KEY (`emergency_contact_id`) REFERENCES `family_member` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='老人表';

-- =====================================================
-- 第三阶段：更新 family_member 的外键（现在 elder 表已存在）
-- =====================================================

-- 为 family_member 表添加外键约束
ALTER TABLE `family_member` ADD CONSTRAINT `fk_family_member_elder_id` 
    FOREIGN KEY (`elder_id`) REFERENCES `elder` (`id`) ON DELETE CASCADE;

-- 为 doctor 和 family_member 添加与 user 表的外键关联
ALTER TABLE `doctor` ADD CONSTRAINT `fk_doctor_user_id` 
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL;

ALTER TABLE `family_member` ADD CONSTRAINT `fk_family_member_user_id` 
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL;

-- 兼容已有数据库：允许 elder_id 为空（取消关联时清空）
ALTER TABLE `family_member` MODIFY COLUMN `elder_id` INT NULL COMMENT '关联的老人ID';

-- =====================================================
-- 第四阶段：创建依赖 elder 和 doctor 的关联表
-- =====================================================

-- 5. 老人-医生关联表
CREATE TABLE `elder_doctor_relation` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `elder_id` INT NOT NULL COMMENT '老人ID',
    `doctor_id` INT NOT NULL COMMENT '医生ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_elder_id` (`elder_id`),
    KEY `idx_doctor_id` (`doctor_id`),
    CONSTRAINT `fk_relation_elder_id` FOREIGN KEY (`elder_id`) REFERENCES `elder` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_relation_doctor_id` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='老人-医生关联表';

-- 6. 老人病史表
CREATE TABLE `elder_medical_history` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '病史ID',
    `elder_id` INT NOT NULL COMMENT '老人ID',
    `disease_name` VARCHAR(100) NOT NULL COMMENT '疾病名称',
    `diagnosed_at` DATE NULL COMMENT '确诊日期',
    `description` TEXT NULL COMMENT '备注说明',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_elder_id` (`elder_id`),
    CONSTRAINT `fk_medical_history_elder_id` FOREIGN KEY (`elder_id`) REFERENCES `elder` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='老人病史表';

-- 7. 健康数据表
CREATE TABLE `health_data` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `elder_id` INT NOT NULL COMMENT '老人ID',
    `heart_rate` INT NULL COMMENT '心率',
    `systolic_pressure` INT NULL COMMENT '收缩压',
    `diastolic_pressure` INT NULL COMMENT '舒张压',
    `steps` INT DEFAULT 0 COMMENT '步数',
    `acceleration` VARCHAR(50) NULL COMMENT '加速度（三轴合成或原始字符串）',
    `recorded_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '采集时间',
    PRIMARY KEY (`id`),
    KEY `idx_elder_id` (`elder_id`),
    KEY `idx_recorded_at` (`recorded_at`),
    CONSTRAINT `fk_health_data_elder_id` FOREIGN KEY (`elder_id`) REFERENCES `elder` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康数据表';

-- 8. 异常事件表
CREATE TABLE `abnormal_event` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '事件ID',
    `elder_id` INT NOT NULL COMMENT '老人ID',
    `type` ENUM('fall', 'heart_rate_abnormal', 'blood_pressure_abnormal', 'prolonged_inactivity') NOT NULL COMMENT '异常类型',
    `severity` ENUM('critical', 'warning', 'notice', 'normal') NOT NULL COMMENT '严重等级',
    `confidence` FLOAT NULL COMMENT '识别置信度',
    `detected_by` VARCHAR(30) NULL COMMENT '检测智能体',
    `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
    `resolved` TINYINT DEFAULT 0 COMMENT '0未处理 1已处理',
    PRIMARY KEY (`id`),
    KEY `idx_elder_id` (`elder_id`),
    KEY `idx_severity` (`severity`),
    KEY `idx_timestamp` (`timestamp`),
    CONSTRAINT `fk_abnormal_event_elder_id` FOREIGN KEY (`elder_id`) REFERENCES `elder` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='异常事件表';

-- 9. SOS求助记录表
CREATE TABLE `sos_record` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '求助ID',
    `elder_id` INT NOT NULL COMMENT '老人ID',
    `location` VARCHAR(100) NULL COMMENT '定位信息',
    `status` ENUM('PENDING', 'RESPONDED', 'CLOSED') DEFAULT 'PENDING' COMMENT '状态',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '求助时间',
    `resolved_at` DATETIME NULL COMMENT '处理完成时间',
    PRIMARY KEY (`id`),
    KEY `idx_elder_id` (`elder_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_sos_record_elder_id` FOREIGN KEY (`elder_id`) REFERENCES `elder` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='SOS求助记录表';

-- 10. 药品信息表
CREATE TABLE `medication` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '药品ID',
    `elder_id` INT NOT NULL COMMENT '老人ID',
    `name` VARCHAR(50) NOT NULL COMMENT '药品名称',
    `description` VARCHAR(200) NULL COMMENT '药品说明',
    `dosage` VARCHAR(50) NULL COMMENT '服用剂量',
    `frequency` VARCHAR(50) NULL COMMENT '服用频次',
    `time` VARCHAR(50) NULL COMMENT '提醒时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_elder_id` (`elder_id`),
    CONSTRAINT `fk_medication_elder_id` FOREIGN KEY (`elder_id`) REFERENCES `elder` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药品信息表';

-- 11. 用药记录表
CREATE TABLE `medication_record` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `medication_id` INT NOT NULL COMMENT '药品ID',
    `taken_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '服用时间',
    `status` ENUM('taken', 'missed', 'skipped') NOT NULL COMMENT '服用状态',
    PRIMARY KEY (`id`),
    KEY `idx_medication_id` (`medication_id`),
    KEY `idx_taken_at` (`taken_at`),
    CONSTRAINT `fk_medication_record_medication_id` FOREIGN KEY (`medication_id`) REFERENCES `medication` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用药记录表';

-- 12. 应急响应表
CREATE TABLE `emergency_response` (
    `id` VARCHAR(50) NOT NULL COMMENT '响应单号',
    `event_id` INT NOT NULL COMMENT '异常事件ID',
    `ambulance_id` VARCHAR(50) NULL COMMENT '救护车编号',
    `eta` INT NULL COMMENT '预计到达时间（秒）',
    `distance` FLOAT NULL COMMENT '距离（km）',
    `status` ENUM('DISPATCH', 'EN_ROUTE', 'ARRIVED', 'COMPLETED') DEFAULT 'DISPATCH' COMMENT '状态',
    `dispatched_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '调度时间',
    PRIMARY KEY (`id`),
    KEY `idx_event_id` (`event_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_emergency_response_event_id` FOREIGN KEY (`event_id`) REFERENCES `abnormal_event` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应急响应表';

-- =====================================================
-- 测试数据插入
-- =====================================================

-- 1. 插入用户数据
INSERT INTO `user` (`username`, `password`, `user_type`, `name`, `phone`) VALUES
('zhangming', '123456', 'elder', '张明', '13800000001'),
('liuying', '123456', 'elder', '刘英', '13800000005'),
('lisong', '123456', 'family', '李松', '13800000002'),
('wangjing', '123456', 'doctor', '王静', '13800000003'),
('admin', '123456', 'admin', '系统管理员', '13800000000'),
-- 补全缺失的用户账户
('zhaojianguo', '123456', 'doctor', '赵建国', '13800000004'),
('zhangli', '123456', 'family', '张丽', '13900000001'),
('zhangqiang', '123456', 'family', '张强', '13900000002'),
('liujianguo', '123456', 'family', '刘建国', '13900000003');

-- 2. 插入医生数据（关联对应用户）
INSERT INTO `doctor` (`user_id`, `name`, `phone`, `department`) VALUES
(4, '王静', '13800000003', '心血管内科'),
(6, '赵建国', '13800000004', '全科医学科');

-- 3. 插入老人表（emergency_contact_id 先设为 NULL）
INSERT INTO `elder` (`user_id`, `age`, `gender`, `blood_type`, `height`, `weight`, `emergency_contact_id`) VALUES
(1, 78, '男', 'O型', 172.0, 70.0, NULL),
(2, 72, '女', 'A型', 158.0, 62.0, NULL);

-- 4. 插入家属（关联 elder_id 和 user_id）
INSERT INTO `family_member` (`user_id`, `elder_id`, `name`, `relationship`, `phone`) VALUES
(7, 1, '张丽', '女儿', '13900000001'),
(8, 1, '张强', '儿子', '13900000002'),
(9, 2, '刘建国', '儿子', '13900000003');

-- 5. 更新老人的紧急联系人
UPDATE `elder` SET `emergency_contact_id` = 1 WHERE `id` = 1;
UPDATE `elder` SET `emergency_contact_id` = 3 WHERE `id` = 2;

-- 6. 插入老人-医生关联
INSERT INTO `elder_doctor_relation` (`elder_id`, `doctor_id`) VALUES
(1, 1),
(2, 2);

-- 7. 插入老人病史
INSERT INTO `elder_medical_history` (`elder_id`, `disease_name`, `diagnosed_at`, `description`) VALUES
(1, '高血压', '2015-03-10', '长期服用降压药'),
(1, '2型糖尿病', '2018-07-22', '注意饮食控制'),
(2, '冠心病', '2010-11-05', '定期复查心电图');

-- 8. 插入健康数据
INSERT INTO `health_data` (`elder_id`, `heart_rate`, `systolic_pressure`, `diastolic_pressure`, `steps`, `acceleration`, `recorded_at`) VALUES
(1, 78, 135, 85, 3200, '0.12,-0.05,9.81', NOW()),
(1, 82, 142, 88, 3500, '0.08,-0.03,9.82', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 68, 128, 78, 2800, '0.10,-0.04,9.80', NOW());

-- 9. 插入异常事件
INSERT INTO `abnormal_event` (`elder_id`, `type`, `severity`, `confidence`, `detected_by`, `timestamp`, `resolved`) VALUES
(1, 'blood_pressure_abnormal', 'warning', 0.92, 'HealthMonitorAgent', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 0),
(2, 'fall', 'critical', 0.98, 'BehaviorAgent', DATE_SUB(NOW(), INTERVAL 2 HOUR), 1);

-- 10. 插入SOS求助记录
INSERT INTO `sos_record` (`elder_id`, `location`, `status`, `created_at`, `resolved_at`) VALUES
(1, '上海市浦东新区XX路123号', 'RESPONDED', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 5 MINUTE),
(2, '北京市朝阳区XX小区10号楼', 'CLOSED', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 10 MINUTE);

-- 11. 插入药品信息
INSERT INTO `medication` (`elder_id`, `name`, `description`, `dosage`, `frequency`, `time`) VALUES
(1, '硝苯地平片', '降压药', '10mg', '每日1次', '08:00'),
(1, '二甲双胍片', '降糖药', '500mg', '每日2次', '08:00,20:00'),
(2, '阿司匹林肠溶片', '抗血小板聚集', '100mg', '每日1次', '09:00');

-- 12. 插入用药记录
INSERT INTO `medication_record` (`medication_id`, `taken_at`, `status`) VALUES
(1, CONCAT(CURDATE(), ' 08:00:00'), 'taken'),
(2, CONCAT(CURDATE(), ' 08:00:00'), 'taken'),
(2, CONCAT(CURDATE(), ' 20:00:00'), 'missed');

-- 13. 插入应急响应
INSERT INTO `emergency_response` (`id`, `event_id`, `ambulance_id`, `eta`, `distance`, `status`, `dispatched_at`) VALUES
('EMP20260527001', 2, '沪A-1234', 480, 3.2, 'ARRIVED', DATE_SUB(NOW(), INTERVAL 2 HOUR) + INTERVAL 1 MINUTE),
('EMP20260527002', 1, NULL, NULL, NULL, 'DISPATCH', NOW());

-- =====================================================
-- 第五阶段：系统操作日志表（持久化）
-- =====================================================

-- 13. 操作日志表
CREATE TABLE `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `operator` VARCHAR(50) NOT NULL COMMENT '操作人',
    `operation` VARCHAR(500) NOT NULL COMMENT '操作描述',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_operator` (`operator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表';