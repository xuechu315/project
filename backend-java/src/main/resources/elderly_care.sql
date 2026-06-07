/*
Navicat MySQL Data Transfer

Source Server         : mysql-new
Source Server Version : 80022
Source Host           : localhost:3306
Source Database       : elderly_care

Target Server Type    : MYSQL
Target Server Version : 80022
File Encoding         : 65001

Date: 2026-06-07 00:04:25
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for abnormal_event
-- ----------------------------
DROP TABLE IF EXISTS `abnormal_event`;
CREATE TABLE `abnormal_event` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '跌倒/心率异常/血压异常/长时间不动',
  `severity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '紧急/警告/注意/正常',
  `confidence` float DEFAULT NULL,
  `detected_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
  `resolved` int DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_severity` (`severity`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of abnormal_event
-- ----------------------------
INSERT INTO `abnormal_event` VALUES ('1', '4', '跌倒', '紧急', '0.968', '行为分析Agent', '2026-06-01 20:10:53', '0');
INSERT INTO `abnormal_event` VALUES ('2', '4', '血压异常', '紧急', '0.985', '健康监测Agent', '2026-06-01 20:10:53', '0');
INSERT INTO `abnormal_event` VALUES ('3', '5', '血压异常', '警告', '0.892', '健康监测Agent', '2026-06-01 20:10:53', '0');
INSERT INTO `abnormal_event` VALUES ('4', '9', '心率异常', '注意', '0.856', '健康监测Agent', '2026-06-01 20:10:53', '0');
INSERT INTO `abnormal_event` VALUES ('5', '6', '心率异常', '正常', '0.723', '健康监测Agent', '2026-06-01 20:10:53', '0');

-- ----------------------------
-- Table structure for contact_record
-- ----------------------------
DROP TABLE IF EXISTS `contact_record`;
CREATE TABLE `contact_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `family_member_id` int NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'sent',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `acknowledged_at` datetime(6) DEFAULT NULL,
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `target_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `target_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_family_member_id` (`family_member_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of contact_record
-- ----------------------------
INSERT INTO `contact_record` VALUES ('1', '4', '10', 'emergency', 'delivered', '2026-06-01 20:10:53', null, null, null, null);
INSERT INTO `contact_record` VALUES ('2', '5', '11', 'normal', 'read', '2026-06-01 20:10:53', null, null, null, null);

-- ----------------------------
-- Table structure for doctor
-- ----------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of doctor
-- ----------------------------
INSERT INTO `doctor` VALUES ('1', '1', '王建国', '13800138001', '全科', '2026-06-01 20:10:53');
INSERT INTO `doctor` VALUES ('2', '2', '李医生', '13800138002', '内科', '2026-06-01 20:10:53');
INSERT INTO `doctor` VALUES ('3', '16', '梅毛冰', '12345678911', null, '2026-06-05 22:43:33');
INSERT INTO `doctor` VALUES ('4', '18', '维克多', '18066660000', null, '2026-06-06 22:13:44');

-- ----------------------------
-- Table structure for elder
-- ----------------------------
DROP TABLE IF EXISTS `elder`;
CREATE TABLE `elder` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `blood_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `height` float DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `emergency_contact_id` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of elder
-- ----------------------------
INSERT INTO `elder` VALUES ('1', '4', '75', '男', 'B-', '170', '75', null, '2026-06-01 20:10:53');
INSERT INTO `elder` VALUES ('2', '5', '68', '女', 'A+', '158', '55', null, '2026-06-01 20:10:53');
INSERT INTO `elder` VALUES ('3', '6', '75', '男', 'O+', '175', '76', null, '2026-06-01 20:10:53');
INSERT INTO `elder` VALUES ('4', '7', '65', '女', 'AB+', '155', '53', null, '2026-06-01 20:10:53');
INSERT INTO `elder` VALUES ('5', '8', '78', '男', 'O-', '168', '65', null, '2026-06-01 20:10:53');
INSERT INTO `elder` VALUES ('6', '9', '71', '女', 'A-', '160', '58', null, '2026-06-01 20:10:53');
INSERT INTO `elder` VALUES ('7', '14', '60', '女', 'B+', '183', '60', null, '2026-06-02 18:25:54');
INSERT INTO `elder` VALUES ('8', '15', '88', '男', 'O-', '177', '55', null, '2026-06-02 19:18:59');
INSERT INTO `elder` VALUES ('9', '17', '70', '男', 'O+', '170', '68', null, '2026-06-06 10:35:43');

-- ----------------------------
-- Table structure for elder_doctor_relation
-- ----------------------------
DROP TABLE IF EXISTS `elder_doctor_relation`;
CREATE TABLE `elder_doctor_relation` (
  `id` int NOT NULL AUTO_INCREMENT,
  `elder_id` int NOT NULL,
  `doctor_id` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_elder_doctor` (`elder_id`,`doctor_id`) USING BTREE,
  UNIQUE KEY `UKemc05j7o39dv9y91gf88guu31` (`elder_id`,`doctor_id`) USING BTREE,
  KEY `idx_elder_id` (`elder_id`) USING BTREE,
  KEY `idx_doctor_id` (`doctor_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of elder_doctor_relation
-- ----------------------------
INSERT INTO `elder_doctor_relation` VALUES ('11', '1', '1', '2026-06-03 01:49:40');
INSERT INTO `elder_doctor_relation` VALUES ('12', '2', '1', '2026-06-03 01:49:40');
INSERT INTO `elder_doctor_relation` VALUES ('14', '4', '2', '2026-06-03 01:49:40');
INSERT INTO `elder_doctor_relation` VALUES ('15', '5', '2', '2026-06-03 01:49:40');
INSERT INTO `elder_doctor_relation` VALUES ('17', '7', '1', '2026-06-05 20:28:47');
INSERT INTO `elder_doctor_relation` VALUES ('19', '8', '3', '2026-06-05 22:43:49');
INSERT INTO `elder_doctor_relation` VALUES ('20', '6', '2', '2026-06-06 10:11:52');
INSERT INTO `elder_doctor_relation` VALUES ('24', '9', '3', '2026-06-06 22:33:32');
INSERT INTO `elder_doctor_relation` VALUES ('25', '3', '4', '2026-06-06 23:59:47');

-- ----------------------------
-- Table structure for elder_family
-- ----------------------------
DROP TABLE IF EXISTS `elder_family`;
CREATE TABLE `elder_family` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `elder_id` int DEFAULT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `relationship` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_elder_id` (`elder_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of elder_family
-- ----------------------------
INSERT INTO `elder_family` VALUES ('1', '10', '1', '张小明', '儿子', '2026-06-01 20:10:53');
INSERT INTO `elder_family` VALUES ('2', '11', '2', '李小红', '女儿', '2026-06-01 20:10:53');
INSERT INTO `elder_family` VALUES ('3', '12', '3', '王小强', '孙子', '2026-06-01 20:10:53');
INSERT INTO `elder_family` VALUES ('4', '12', '4', '王小强', null, '2026-06-01 20:11:15');
INSERT INTO `elder_family` VALUES ('7', '13', '5', '李姐', null, '2026-06-01 20:11:58');
INSERT INTO `elder_family` VALUES ('8', '12', '7', '王小强', null, '2026-06-02 18:26:31');
INSERT INTO `elder_family` VALUES ('9', '10', '8', '张小明', null, '2026-06-02 19:18:59');
INSERT INTO `elder_family` VALUES ('10', '13', '6', '李姐', null, '2026-06-02 19:22:38');
INSERT INTO `elder_family` VALUES ('11', '10', '6', '张小明', null, '2026-06-05 20:33:24');
INSERT INTO `elder_family` VALUES ('15', '13', '9', '李姐', null, '2026-06-06 22:33:32');

-- ----------------------------
-- Table structure for health_data
-- ----------------------------
DROP TABLE IF EXISTS `health_data`;
CREATE TABLE `health_data` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `heart_rate` int DEFAULT NULL,
  `systolic_pressure` int DEFAULT NULL,
  `diastolic_pressure` int DEFAULT NULL,
  `steps` int DEFAULT '0',
  `recorded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_recorded_at` (`recorded_at`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of health_data
-- ----------------------------
INSERT INTO `health_data` VALUES ('1', '4', '118', '185', '110', '1250', '2026-06-01 20:10:53');
INSERT INTO `health_data` VALUES ('2', '5', '92', '142', '88', '3102', '2026-06-01 20:10:53');
INSERT INTO `health_data` VALUES ('3', '6', '78', '125', '82', '4521', '2026-06-01 20:10:53');
INSERT INTO `health_data` VALUES ('4', '7', '72', '118', '78', '5890', '2026-06-01 20:10:53');
INSERT INTO `health_data` VALUES ('5', '8', '68', '130', '85', '2340', '2026-06-01 20:10:53');
INSERT INTO `health_data` VALUES ('6', '9', '85', '135', '88', '4156', '2026-06-01 20:10:53');
INSERT INTO `health_data` VALUES ('7', '4', '75', '120', '80', '4520', '2026-04-27 08:00:00');
INSERT INTO `health_data` VALUES ('8', '4', '78', '125', '82', '5100', '2026-04-27 10:00:00');
INSERT INTO `health_data` VALUES ('9', '4', '82', '130', '85', '3800', '2026-04-27 12:00:00');
INSERT INTO `health_data` VALUES ('10', '4', '76', '122', '81', '4200', '2026-04-27 14:00:00');
INSERT INTO `health_data` VALUES ('11', '4', '80', '128', '84', '3500', '2026-04-27 16:00:00');
INSERT INTO `health_data` VALUES ('12', '4', '85', '135', '88', '2800', '2026-04-27 18:00:00');
INSERT INTO `health_data` VALUES ('13', '4', '72', '118', '78', '1200', '2026-04-27 20:00:00');
INSERT INTO `health_data` VALUES ('14', '4', '68', '115', '75', '500', '2026-04-27 22:00:00');
INSERT INTO `health_data` VALUES ('15', '4', '70', '118', '78', '3200', '2026-04-28 06:00:00');
INSERT INTO `health_data` VALUES ('16', '4', '75', '122', '80', '4100', '2026-04-28 08:00:00');
INSERT INTO `health_data` VALUES ('17', '4', '88', '145', '92', '2800', '2026-04-28 10:00:00');
INSERT INTO `health_data` VALUES ('18', '4', '118', '185', '110', '1250', '2026-04-28 10:24:00');

-- ----------------------------
-- Table structure for medical_history
-- ----------------------------
DROP TABLE IF EXISTS `medical_history`;
CREATE TABLE `medical_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `elder_id` int NOT NULL COMMENT '老人ID',
  `disease_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '疾病名称',
  `diagnosis_date` date DEFAULT NULL COMMENT '诊断日期',
  `treatment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '治疗方案',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态：痊愈/治疗中/慢性病/观察中',
  `notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_elder_id` (`elder_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of medical_history
-- ----------------------------
INSERT INTO `medical_history` VALUES ('4', '2', '关节炎', '2018-11-05', '物理治疗+药物治疗', '慢性病', '阴雨天注意保暖', '2026-06-03 21:15:13', '2026-06-03 21:15:13');
INSERT INTO `medical_history` VALUES ('5', '2', '高血压', '2022-01-20', '服用降压药', '治疗中', '血压控制良好', '2026-06-03 21:15:13', '2026-06-03 21:15:13');
INSERT INTO `medical_history` VALUES ('6', '3', '胃溃疡', '2023-02-15', '药物治疗+饮食调理', '痊愈', '已康复，注意饮食', '2026-06-03 21:15:13', '2026-06-03 21:15:13');
INSERT INTO `medical_history` VALUES ('9', '6', '白内障', '2022-12-05', '手术治疗', '痊愈', '术后恢复良好', '2026-06-03 21:15:13', '2026-06-03 21:15:13');
INSERT INTO `medical_history` VALUES ('55', '4', '骨质疏松', '2020-06-10', '补钙+维生素D', '慢性病', '注意防跌倒', '2026-06-06 21:18:53', '2026-06-06 21:18:53');
INSERT INTO `medical_history` VALUES ('56', '4', '高血压', '2025-09-02', '吃高血压药', '慢性病', '少吃重油重盐的食物', '2026-06-06 21:18:53', '2026-06-06 21:18:53');
INSERT INTO `medical_history` VALUES ('63', '1', '高血压', '2020-03-15', '长期服用降压药', '观察中', '需定期监测血压', '2026-06-06 23:26:46', '2026-06-06 23:26:46');
INSERT INTO `medical_history` VALUES ('64', '1', '糖尿病', '2019-08-20', '口服降糖药+饮食控制', '慢性病', '注意饮食，定期检测血糖', '2026-06-06 23:26:46', '2026-06-06 23:26:46');
INSERT INTO `medical_history` VALUES ('65', '1', '冠心病', '2021-05-10', '药物治疗', '治疗中', '避免剧烈运动', '2026-06-06 23:26:46', '2026-06-06 23:26:46');
INSERT INTO `medical_history` VALUES ('66', '5', '前列腺增生', '2021-09-20', '药物治疗', '治疗中', '定期复查', '2026-06-06 23:56:41', '2026-06-06 23:56:41');

-- ----------------------------
-- Table structure for medication
-- ----------------------------
DROP TABLE IF EXISTS `medication`;
CREATE TABLE `medication` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dosage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `frequency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提醒时间，如 08:00',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of medication
-- ----------------------------
INSERT INTO `medication` VALUES ('1', '4', '硝苯地平缓释片', '降压药', '1片', '每日3次', '午餐后', '2026-06-01 20:10:53');
INSERT INTO `medication` VALUES ('2', '4', '阿司匹林肠溶片', '抗血小板聚集', '1片', '每日1次', '晚餐后', '2026-06-01 20:10:53');
INSERT INTO `medication` VALUES ('3', '5', '硝苯地平缓释片', '降压药', '1片', '每日1次', '08:00', '2026-06-01 20:10:53');
INSERT INTO `medication` VALUES ('4', '9', '二甲双胍', '降糖药', '1片', '每日2次', '早晚各一次', '2026-06-01 20:10:53');
INSERT INTO `medication` VALUES ('7', '10', '氯雷他定', '过敏药', '每次1片', '每日1次', '午餐后', '2026-06-05 23:41:15');
INSERT INTO `medication` VALUES ('8', '7', '硝苯地平缓释片', '降压药', '每次2片', '每日1次', '午餐后', '2026-06-06 21:17:29');
INSERT INTO `medication` VALUES ('9', '9', '头孢', '治感冒', '每次1片', '每日1次', '午餐后', '2026-06-06 23:57:37');

-- ----------------------------
-- Table structure for medication_record
-- ----------------------------
DROP TABLE IF EXISTS `medication_record`;
CREATE TABLE `medication_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `medication_id` int NOT NULL,
  `taken_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '已服用/漏服/跳过',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_medication_id` (`medication_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of medication_record
-- ----------------------------
INSERT INTO `medication_record` VALUES ('1', '1', '2026-04-28 08:00:00', '已服用');
INSERT INTO `medication_record` VALUES ('2', '2', '2026-04-28 09:00:00', '已服用');
INSERT INTO `medication_record` VALUES ('3', '3', '2026-04-28 08:00:00', '已服用');
INSERT INTO `medication_record` VALUES ('4', '4', '2026-04-28 08:00:00', '已服用');
INSERT INTO `medication_record` VALUES ('5', '4', '2026-04-28 20:00:00', '漏服');
INSERT INTO `medication_record` VALUES ('6', '5', '2026-04-28 08:00:00', '已服用');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `operation` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_created_at` (`created_at`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES ('6', 'admin', '编辑老人账号: 张大爷 家属=张小明 医生=李医生', '2026-06-01 20:12:48');
INSERT INTO `operation_log` VALUES ('7', 'admin', '解绑: 孙桂英 家属=无 医生=王建国', '2026-06-01 20:12:57');
INSERT INTO `operation_log` VALUES ('8', 'admin', '修改绑定: 孙桂英 家属=无 医生=李医生', '2026-06-01 20:13:03');
INSERT INTO `operation_log` VALUES ('9', 'admin', '编辑家属账号: 李姐 → 关联老人: 赵德明', '2026-06-01 23:21:53');
INSERT INTO `operation_log` VALUES ('10', 'admin', '新增老人账号: qc 家属=李小红 医生=王建国', '2026-06-02 18:25:54');
INSERT INTO `operation_log` VALUES ('11', 'admin', '编辑老人账号: qc 家属=王小强 医生=李医生', '2026-06-02 18:26:31');
INSERT INTO `operation_log` VALUES ('12', 'admin', '新增老人账号: 王兵', '2026-06-02 19:18:59');
INSERT INTO `operation_log` VALUES ('13', 'admin', '解绑: 孙桂英 家属=无 医生=李医生', '2026-06-02 19:19:45');
INSERT INTO `operation_log` VALUES ('14', 'admin', '修改绑定: 孙桂英 家属=李姐 医生=无', '2026-06-02 19:22:38');
INSERT INTO `operation_log` VALUES ('15', 'admin', '修改绑定: qc 家属=王小强 医生=王建国', '2026-06-05 20:28:47');
INSERT INTO `operation_log` VALUES ('16', 'admin', '编辑老人账号: 孙桂英 家属=张小明、李姐 医生=王建国', '2026-06-05 20:33:24');
INSERT INTO `operation_log` VALUES ('17', 'admin', '新增医生账号: 梅毛冰', '2026-06-05 22:43:33');
INSERT INTO `operation_log` VALUES ('18', 'admin', '修改绑定: 王兵 家属=张小明 医生=梅毛冰', '2026-06-05 22:43:49');
INSERT INTO `operation_log` VALUES ('19', 'admin', '编辑老人账号: 孙桂英 家属=张小明、李姐 医生=李医生', '2026-06-06 10:11:52');
INSERT INTO `operation_log` VALUES ('20', 'admin', '编辑家属账号: 张小明 → 关联老人: 张大爷、孙桂英、王兵', '2026-06-06 10:14:04');
INSERT INTO `operation_log` VALUES ('21', 'admin', '新增老人账号: 李爱国 家属=李姐 医生=梅毛冰', '2026-06-06 10:35:43');
INSERT INTO `operation_log` VALUES ('22', 'admin', '解绑: 李爱国', '2026-06-06 17:22:49');
INSERT INTO `operation_log` VALUES ('23', 'admin', '编辑用户账号: 李爱国', '2026-06-06 17:23:03');
INSERT INTO `operation_log` VALUES ('24', 'admin', '新增用户账号: 维克多', '2026-06-06 22:13:44');
INSERT INTO `operation_log` VALUES ('25', 'admin', '解绑: 李爱国', '2026-06-06 22:18:00');
INSERT INTO `operation_log` VALUES ('26', 'admin', '修改绑定: 李爱国', '2026-06-06 22:18:09');
INSERT INTO `operation_log` VALUES ('27', 'admin', '编辑用户账号: 王建国', '2026-06-06 22:18:27');
INSERT INTO `operation_log` VALUES ('28', 'admin', '解绑: 李爱国', '2026-06-06 22:33:19');
INSERT INTO `operation_log` VALUES ('29', 'admin', '李爱国 家属=李姐 医生=梅毛冰', '2026-06-06 22:33:32');
INSERT INTO `operation_log` VALUES ('30', 'admin', '王林 家属=王小强 医生=维克多', '2026-06-06 23:59:47');

-- ----------------------------
-- Table structure for sos_record
-- ----------------------------
DROP TABLE IF EXISTS `sos_record`;
CREATE TABLE `sos_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT 'PENDING/已响应/已闭环',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `resolved_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sos_record
-- ----------------------------
INSERT INTO `sos_record` VALUES ('1', '4', '康乐园小区 A3-102室', 'PENDING', '2026-06-01 20:10:53', null);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'elder/family/doctor/admin',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'doctor1', '$2a$10$eC.Q/xu1fOB6CbOio13k4uju4cP5WUSJZIiVy6mqVmdKINt2tFLMa', 'doctor', '王建国', '13800000011', '2026-06-06 00:29:11');
INSERT INTO `user` VALUES ('2', 'doctor2', '$2a$10$g5COET2Qc05JPfP3AD3FjeXk8M.2bIcmBp.kaQ2lAqennPjAySHqi', 'doctor', '李医生', '13800138002', '2026-06-01 20:10:53');
INSERT INTO `user` VALUES ('3', 'admin', '$2a$10$dvqiSBFlbkv3VtUVx6vcLex67pII.Icga.w7HqyfxI.csSyWjftpG', 'admin', '系统管理员', '13800138000', '2026-06-01 20:10:53');
INSERT INTO `user` VALUES ('4', 'elderly1', '$2a$10$Y38luDJ/QixowOJWd5D9Lur9KxU0MzMtmmw3sggWBRxpgo.OfCxgi', 'elder', '张大爷', '13900000000', '2026-06-01 20:10:53');
INSERT INTO `user` VALUES ('5', 'elderly2', '$2a$10$TcCMnK28nKT/xfHxC.zOPu0W12/c1.24EZxxwuh9zS56GEXo1zB/a', 'elder', '李芳', '13900000002', '2026-06-01 20:10:53');
INSERT INTO `user` VALUES ('6', 'elderly3', '123456', 'elder', '王林', '13900000001', '2026-06-01 20:10:53');
INSERT INTO `user` VALUES ('7', 'elderly4', '123456', 'elder', '陈秀莲', '18000005555', '2026-06-01 20:10:53');
INSERT INTO `user` VALUES ('8', 'elderly5', '123456', 'elder', '赵德明', '13900000088', '2026-06-01 20:10:53');
INSERT INTO `user` VALUES ('9', 'elderly6', '123456', 'elder', '孙桂英', '13900000006', '2026-06-01 20:10:53');
INSERT INTO `user` VALUES ('10', 'family1', '$2a$10$ubahHrrgIeyEDTnEfmEOUu.BpzZToKgqL13Jb1MueFtZZB0A7/DbG', 'family', '张小明', '13700000001', '2026-06-01 20:10:53');
INSERT INTO `user` VALUES ('11', 'family2', '123456', 'family', '李小红', '13700000002', '2026-06-01 20:10:53');
INSERT INTO `user` VALUES ('12', 'family3', '123456', 'family', '王小强', '13700000003', '2026-06-01 20:10:53');
INSERT INTO `user` VALUES ('13', 'lijie', '$2a$10$TZChJrDNI1SWpCWcamYWrOVElIPbMj8LFlYwmqL7Ea5jWArmiyuGS', 'family', '李姐', '13700000006', '2026-06-01 20:11:58');
INSERT INTO `user` VALUES ('14', 'qcc', '$2a$10$C5NSl5Qbc1Qm2Tmqm7FXNe8T0l0E6ZIbbKjnByZdioiLEftW5Exli', 'elder', 'qc', '18251363010', '2026-06-02 18:25:54');
INSERT INTO `user` VALUES ('15', 'wangbing', '$2a$10$/ohEE61HOmXqBVCKK7crDu4U02JqBKLCtzMKHj8af61qIs1mnZaz2', 'elder', '王兵', '15000000088', '2026-06-02 19:18:59');
INSERT INTO `user` VALUES ('16', 'doctor3', '$2a$10$/BzB.taf2le1YNIykDnSHO8py8vOkWrF0e9tvDo8UUJfUEZFJ5j7O', 'doctor', '梅毛冰', '12345678911', '2026-06-05 22:43:33');
INSERT INTO `user` VALUES ('17', 'liaiguo', '$2a$10$JLoPqSVpdSTZ5bzGDGuQM.RGm..0xl6GoWj.tLn5XUZOCvcLBURZ6', 'elder', '李爱国', '18022225500', '2026-06-06 10:35:43');
INSERT INTO `user` VALUES ('18', 'weikeduo', '$2a$10$MiG.ZQElcdsGjcijkP5pXuhQvLL7ynXYT5Mj2byGlqrBH.9jcICAe', 'doctor', '维克多', '18066660000', '2026-06-06 22:13:44');
