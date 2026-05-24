/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80019
 Source Host           : localhost:3306
 Source Schema         : attendance_manager

 Target Server Type    : MySQL
 Target Server Version : 80019
 File Encoding         : 65001

 Date: 19/12/2025 18:35:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for check1
-- ----------------------------
DROP TABLE IF EXISTS `check1`;
CREATE TABLE `check1`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `employeeID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `date` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `checkOnTime` datetime NULL DEFAULT NULL,
  `checkOffTime` datetime NULL DEFAULT NULL,
  `checkOnStatus` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `checkOffStatus` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of check1
-- ----------------------------
INSERT INTO `check1` VALUES ('7513e7d8-1155-4870-a91a-79f77b19e9eb', '10002', '2025-06-17', '', '2025-06-17 23:54:01', '2025-06-17 23:54:03', '杩熷埌', '姝ｅ父');
INSERT INTO `check1` VALUES ('7b43fa1f-780f-4019-8aa1-a91b1dd2787d', '10005', '2025-06-16', '', '2025-06-16 19:41:16', '2025-06-16 19:41:58', '杩熷埌', '姝ｅ父');
INSERT INTO `check1` VALUES ('cb8e362b-33e9-4361-a8fa-b2320da317e2', '10001', '2025-06-19', '', '2025-06-19 18:33:24', '2025-06-19 18:33:32', '杩熷埌', '姝ｅ父');

-- ----------------------------
-- Table structure for checkrepair
-- ----------------------------
DROP TABLE IF EXISTS `checkrepair`;
CREATE TABLE `checkrepair`  (
  `id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `applyNumber` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `taskTypeID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `repairDate` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `repairType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `checkOnTime` datetime NULL DEFAULT NULL,
  `checkOffTime` datetime NULL DEFAULT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `approvalID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `applyTime` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of checkrepair
-- ----------------------------
INSERT INTO `checkrepair` VALUES ('cr000001-0000-4000-8000-000000000101', '10003', '4', '2026-05-18', '上班补卡', '2026-05-18 08:28:00', NULL, '外勤拜访客户，忘记上班打卡', '1', 'tsk-repair-approve-10003', '2026-05-18 10:05:00');
INSERT INTO `checkrepair` VALUES ('cr000001-0000-4000-8000-000000000102', '10004', '4', '2026-05-19', '下班补卡', NULL, '2026-05-19 18:12:00', '下班时打卡机网络异常', '0', 'tsk-repair-pending-10004', '2026-05-19 18:30:00');
INSERT INTO `checkrepair` VALUES ('cr000001-0000-4000-8000-000000000103', '10007', '4', '2026-05-16', '上下班补卡', '2026-05-16 08:40:00', '2026-05-16 17:55:00', '个人原因忘记打卡', '2', 'tsk-repair-reject-10007', '2026-05-16 18:20:00');

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer`  (
  `id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `applyNumber` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer
-- ----------------------------
INSERT INTO customer VALUES ('1c6694ba-aea5-4be7-a063-81d5d4c0d81a', '88877', '王先生', '18877777777', '北京市海淀区', '个人', '', '10001');
INSERT INTO customer VALUES ('e1840f51-ba25-4e5d-ba9c-19e1aa526d6d', '7775', '刘客户', '15877777777', '北京市朝阳区', '个人', '大老板', '10002');

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `quantity` int(0) NULL DEFAULT NULL,
  `posNum` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO department VALUES ('128216ff-8d37-4c6c-b468-96391bd5f09a', '100', '管理部', 1, 1);
INSERT INTO `department` VALUES ('7ca44c62-7aa1-41dd-ba3e-4bea2c308f26', '101', '鎶€鏈儴', 1, 1);
INSERT INTO department VALUES ('e9b58925-b1a7-4aa1-9eed-bb01eab41ce3', '102', '人事部', 1, 1);

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `employeeNumber` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `employeeName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `birthday` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `departmentID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `employeePositionID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `employeeType` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `entryDate` datetime NULL DEFAULT NULL,
  `IDNumber` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `education` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `marriage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `workStatus` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO employee VALUES ('6b8bf6e5-9dec-4694-a49a-8a04f173e39e', '10002', '李四', '女', '2002-06-05', '7ca44c62-7aa1-41dd-ba3e-4bea2c308f26', 'e5e8993c-243c-4c3f-86c2-b8e4802dd55a', '2', '15977777777', '北京市朝阳区', '123456', '2025-06-17 00:00:00', '110222200206057777', '大专', '未婚', '0');
INSERT INTO employee VALUES ('af7a4c44-6f55-404f-b37f-efa673c76dab', '10001', '张三', '男', '2000-10-10', '128216ff-8d37-4c6c-b468-96391bd5f09a', 'cbfec9b8-d693-41aa-80e6-c53dd03acecb', '3', '15511111111', '北京市海淀区', '123456', '2025-06-16 00:00:00', '110555200010105555', '本科', '未婚', '0');

-- ----------------------------
-- Table structure for employeetype
-- ----------------------------
DROP TABLE IF EXISTS `employeetype`;
CREATE TABLE `employeetype`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `quantity` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employeetype
-- ----------------------------
INSERT INTO `employeetype` VALUES ('1', '0', '鍏朵粬宀椾綅', 0);
INSERT INTO employeetype VALUES ('2', '0', '技工岗位', 1);
INSERT INTO `employeetype` VALUES ('3', '0', '绠＄悊宀椾綅', 2);
INSERT INTO employeetype VALUES ('4', '0', '普通岗位', 0);

-- ----------------------------
-- Table structure for fixedassets
-- ----------------------------
DROP TABLE IF EXISTS `fixedassets`;
CREATE TABLE `fixedassets`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `typeID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `employeeNumber` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `taskTypeID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `approvalID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `applyTime` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fixedassets
-- ----------------------------
INSERT INTO fixedassets VALUES ('0766f2aa-5265-409a-a50b-b9685e1ac378', '55551', '会议室01', '4', 300000.00, '10001', '2', '1', 'efa673c76dab', '2025-06-17 23:24:11');
INSERT INTO fixedassets VALUES ('498d5ce8-c695-473b-841b-e04c6fe24926', '99991', '笔记本电脑A', '2', 4560.00, '10001', '2', '1', 'efa673c76dab', '2025-06-17 23:18:36');
INSERT INTO fixedassets VALUES ('fc3eb15f-43b4-415f-b59e-2e624f11d176', '5559', '笔记本电脑B', '2', 5456.00, '10002', '2', '0', 'b4943625-efd2-411e-b569-6f9b36c43843', '2025-06-17 23:52:31');

-- ----------------------------
-- Table structure for fixedassettype
-- ----------------------------
DROP TABLE IF EXISTS `fixedassettype`;
CREATE TABLE `fixedassettype`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `quantity` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fixedassettype
-- ----------------------------
INSERT INTO `fixedassettype` VALUES ('1', '0', '杞欢', 0);
INSERT INTO `fixedassettype` VALUES ('2', '0', '纭欢', 0);
INSERT INTO `fixedassettype` VALUES ('3', '0', '鍏朵粬', 0);
INSERT INTO fixedassettype VALUES ('4', '0', '会议室', 0);

-- ----------------------------
-- Table structure for leave1
-- ----------------------------
DROP TABLE IF EXISTS `leave1`;
CREATE TABLE `leave1`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `beginTime` datetime NULL DEFAULT NULL,
  `endTime` datetime NULL DEFAULT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `applyNumber` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `duration` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `typeID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `approvalID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `taskTypeID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `applyTime` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of leave1
-- ----------------------------
INSERT INTO `leave1` VALUES ('651c9c85-2282-4ef4-ad6b-bcff1cabf701', '2025-01-01 23:19:08', '2025-01-05 23:19:14', '鍑哄幓鍔炰簨', '10001', '72', '2', '2', '2f1a2e51-ddf0-4d29-be44-4ac2cd5de2e8', '1', '2025-06-17 23:19:02');
INSERT INTO `leave1` VALUES ('f9603ba9-c4df-45a8-bb42-87c25e1ff1db', '2025-01-09 00:00:00', '2025-01-011 23:53:37', '璇峰亣', '10002', '191', '4', '0', '74e3a66c-7d50-42c8-9384-e9633b91d947', '1', '2025-06-17 23:53:01');

-- ----------------------------
-- Table structure for leavetype
-- ----------------------------
DROP TABLE IF EXISTS `leavetype`;
CREATE TABLE `leavetype`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of leavetype
-- ----------------------------
INSERT INTO `leavetype` VALUES ('1', '0', '涓у亣');
INSERT INTO `leavetype` VALUES ('2', '0', '浜嬪亣');
INSERT INTO `leavetype` VALUES ('3', '0', '濠氬亣');
INSERT INTO `leavetype` VALUES ('4', '0', '骞村亣');
INSERT INTO `leavetype` VALUES ('5', '0', '鐥呭亣');

-- ----------------------------
-- Table structure for meeting
-- ----------------------------
DROP TABLE IF EXISTS `meeting`;
CREATE TABLE `meeting`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  `roomID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `participants` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `details` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `publisherNumber` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `publishTime` datetime NULL DEFAULT NULL,
  `beginDate` date NULL DEFAULT NULL,
  `endDate` date NULL DEFAULT NULL,
  `beginTime` datetime NULL DEFAULT NULL,
  `endTime` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of meeting
-- ----------------------------
INSERT INTO `meeting` VALUES ('43ae9611-7f44-4947-af5c-90c29a7c0d12', '鑰冨嫟绯荤粺涓婄嚎', NULL, NULL, '', '', '鑰冨嫟绯荤粺1.0涓庝粖鏃ヨ捣涓婄嚎锛岃鍚勪綅鎸夋椂鎵撳崱绛惧埌', '2', '10001', NULL, '2024-12-18', '2025-09-19', NULL, NULL);
INSERT INTO `meeting` VALUES ('c861a218-3628-4f96-8d7a-41a3e374e3aa', '鍏充簬绗竴瀛ｅ害宸ヤ綔閮ㄧ讲浼氳', NULL, '0766f2aa-5265-409a-a50b-b9685e1ac378', '', '', '', '1', '10001', NULL, NULL, NULL, '2024-12-27 13:26:14', '2024-12-27 16:26:23');

-- ----------------------------
-- Table structure for position
-- ----------------------------
DROP TABLE IF EXISTS `position`;
CREATE TABLE `position`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `monthlySalary` int(0) NULL DEFAULT NULL,
  `quantity` int(0) NULL DEFAULT NULL,
  `departmentID` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `typeID` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of position
-- ----------------------------
INSERT INTO position VALUES ('2a0a0738-5822-4732-ae80-b279430ed67a', '1001', '系统管理员', 16000, 0, '', '3');
INSERT INTO position VALUES ('5a34a879-6523-4280-864a-7d501a2502be', '10001', '系统管理员', 16000, 0, '', '3');
INSERT INTO `position` VALUES ('819b5c90-345f-4079-83d5-8339ffd61368', '50001', '浜轰簨缁忕悊', 5000, 1, 'e9b58925-b1a7-4aa1-9eed-bb01eab41ce3', '3');
INSERT INTO position VALUES ('cbfec9b8-d693-41aa-80e6-c53dd03acecb', '1002', '系统管理员', 15000, 1, '128216ff-8d37-4c6c-b468-96391bd5f09a', '3');
INSERT INTO `position` VALUES ('e5e8993c-243c-4c3f-86c2-b8e4802dd55a', '80001', '寮€鍙戝伐绋嬪笀', 80000, 1, '7ca44c62-7aa1-41dd-ba3e-4bea2c308f26', '2');

-- ----------------------------
-- Table structure for salary
-- ----------------------------
DROP TABLE IF EXISTS `salary`;
CREATE TABLE `salary`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `monthlySalary` int(0) NULL DEFAULT NULL,
  `workDays` int(0) NULL DEFAULT NULL,
  `checkDays` int(0) NULL DEFAULT NULL,
  `leaveDays` int(0) NULL DEFAULT NULL,
  `salary` decimal(10, 2) NULL DEFAULT NULL,
  `employeeID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `month` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `lateTimes` int(0) NULL DEFAULT NULL,
  `leaveEarlyTimes` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of salary
-- ----------------------------
INSERT INTO `salary` VALUES ('83aee5c7-5ac0-4274-af1d-43f845bc07d0', 5000, 20, 1, 1, 200.00, '10005', '2025-06', 1, 0);
INSERT INTO `salary` VALUES ('b7207ad8-4240-4a18-a2a9-a75ea184daf3', 15000, 20, 0, 0, 0.00, '10001', '2025-06', 0, 0);
INSERT INTO `salary` VALUES ('cb8095c4-1b43-4edc-9d8f-292f01e9b0ae', 80000, 22, 0, 0, 0.00, '10002', '2025-06', 0, 0);

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `typeID` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `applyNumber` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `approvalNumber` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `applyTime` datetime NULL DEFAULT NULL,
  `approvalTime` datetime NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `applyID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `receiveNumber` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task
-- ----------------------------
INSERT INTO task VALUES ('2f1a2e51-ddf0-4d29-be44-4ac2cd5de2e8', NULL, '张三的请假申请', '1', '10001', '10001', '2025-06-17 23:19:02', '2025-06-17 23:19:49', '2', '651c9c85-2282-4ef4-ad6b-bcff1cabf701', '10001');
INSERT INTO task VALUES ('74e3a66c-7d50-42c8-9384-e9633b91d947', NULL, '李四的请假申请', '1', '10002', NULL, '2025-06-17 23:53:01', NULL, '0', 'f9603ba9-c4df-45a8-bb42-87c25e1ff1db', '10001');
INSERT INTO task VALUES ('b4943625-efd2-411e-b569-6f9b36c43843', NULL, '李四的固定资产购置申请', '2', '10002', NULL, '2025-06-17 23:52:31', NULL, '0', 'fc3eb15f-43b4-415f-b59e-2e624f11d176', '10001');

-- ----------------------------
-- Table structure for tasktype
-- ----------------------------
DROP TABLE IF EXISTS `tasktype`;
CREATE TABLE `tasktype`  (
  `id` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `number` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tasktype
-- ----------------------------
INSERT INTO `tasktype` VALUES ('1', '0', '请假申请');
INSERT INTO `tasktype` VALUES ('2', '0', '固定资产购置申请');
INSERT INTO `tasktype` VALUES ('3', '0', '固定资产报废申请');
INSERT INTO `tasktype` VALUES ('4', '0', '考勤补卡申请');

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Demo data supplement (2026)
-- ----------------------------

-- Position supplements for normal employees (type 1 / type 4)
INSERT INTO `position` VALUES ('9f0f8dc2-73e0-4a01-b72f-4b35b00a1001', '90001', 'Technical Specialist', 9000, 0, '7ca44c62-7aa1-41dd-ba3e-4bea2c308f26', '1');
INSERT INTO `position` VALUES ('9f0f8dc2-73e0-4a01-b72f-4b35b00a1002', '90002', 'General Staff', 6500, 0, 'e9b58925-b1a7-4aa1-9eed-bb01eab41ce3', '4');

-- Employee supplements for role-based and statistical pages
INSERT INTO `employee` VALUES ('11111111-1111-4111-8111-111111111103', '10003', 'Alice Chen', 'F', '1998-03-15', '7ca44c62-7aa1-41dd-ba3e-4bea2c308f26', '9f0f8dc2-73e0-4a01-b72f-4b35b00a1001', '1', '13800001003', 'Beijing', '123456', '2026-05-08 09:00:00', '110101199803150033', 'Bachelor', 'Single', '0');
INSERT INTO `employee` VALUES ('11111111-1111-4111-8111-111111111104', '10004', 'Bob Wang', 'M', '1993-07-22', '7ca44c62-7aa1-41dd-ba3e-4bea2c308f26', '9f0f8dc2-73e0-4a01-b72f-4b35b00a1001', '1', '13800001004', 'Shanghai', '123456', '2026-04-12 09:00:00', '110101199307220044', 'Master', 'Married', '0');
INSERT INTO `employee` VALUES ('11111111-1111-4111-8111-111111111105', '10005', 'Cathy Liu', 'F', '1985-11-09', 'e9b58925-b1a7-4aa1-9eed-bb01eab41ce3', '819b5c90-345f-4079-83d5-8339ffd61368', '2', '13800001005', 'Guangzhou', '123456', '2026-03-01 09:00:00', '110101198511090055', 'College', 'Married', '0');
INSERT INTO `employee` VALUES ('11111111-1111-4111-8111-111111111106', '10006', 'David Zhao', 'M', '1970-02-14', '128216ff-8d37-4c6c-b468-96391bd5f09a', 'cbfec9b8-d693-41aa-80e6-c53dd03acecb', '3', '13800001006', 'Shenzhen', '123456', '2026-02-10 09:00:00', '110101197002140066', 'PhD', 'Married', '0');
INSERT INTO `employee` VALUES ('11111111-1111-4111-8111-111111111107', '10007', 'Eva Sun', 'F', '2004-09-30', '7ca44c62-7aa1-41dd-ba3e-4bea2c308f26', '9f0f8dc2-73e0-4a01-b72f-4b35b00a1001', '1', '13800001007', 'Chengdu', '123456', '2026-01-15 09:00:00', '110101200409300077', 'HighSchool', 'Single', '0');
INSERT INTO `employee` VALUES ('11111111-1111-4111-8111-111111111108', '10008', 'Frank He', 'M', '2001-06-21', '7ca44c62-7aa1-41dd-ba3e-4bea2c308f26', '9f0f8dc2-73e0-4a01-b72f-4b35b00a1001', '1', '13800001008', 'Hangzhou', '123456', '2025-12-20 09:00:00', '110101200106210088', 'Vocational', 'Single', '0');
INSERT INTO `employee` VALUES ('11111111-1111-4111-8111-111111111109', '10009', 'Grace Lin', 'F', '1999-12-01', 'e9b58925-b1a7-4aa1-9eed-bb01eab41ce3', '9f0f8dc2-73e0-4a01-b72f-4b35b00a1002', '4', '13800001009', 'Nanjing', '123456', '2025-11-18 09:00:00', '110101199912010099', 'College', 'Single', '0');

-- Customer supplements
INSERT INTO `customer` VALUES ('cccccccc-0000-4000-8000-000000000301', 'C10003', 'Northwind Tech', '021-60001003', 'Shanghai Pudong', 'Company', 'A-tier account', '10003');
INSERT INTO `customer` VALUES ('cccccccc-0000-4000-8000-000000000302', 'C10004', 'BlueOcean Studio', '020-60001004', 'Guangzhou Tianhe', 'Company', 'Annual contract', '10004');
INSERT INTO `customer` VALUES ('cccccccc-0000-4000-8000-000000000303', 'C10008', 'Luna Personal', '13900001008', 'Hangzhou Xihu', 'Individual', 'Follow-up pending', '10008');

-- Fixed asset supplements (status: 0 pending, 1 approved, 2 rejected, 3 revoked)
INSERT INTO `fixedassets` VALUES ('fa000001-0000-4000-8000-000000000101', 'FA-2026-001', 'Dell Latitude 7450', '2', 9800.00, '10003', '2', '1', 'tsk-fixed-approve-10003', '2026-05-07 10:15:00');
INSERT INTO `fixedassets` VALUES ('fa000001-0000-4000-8000-000000000102', 'FA-2026-002', '4K Monitor', '2', 2800.00, '10004', '2', '0', 'tsk-fixed-pending-10004', '2026-05-10 11:20:00');
INSERT INTO `fixedassets` VALUES ('fa000001-0000-4000-8000-000000000103', 'FA-2026-003', 'Office Desk', '3', 1800.00, '10007', '2', '2', 'tsk-fixed-reject-10007', '2026-05-11 16:05:00');
INSERT INTO `fixedassets` VALUES ('fa000001-0000-4000-8000-000000000104', 'FA-2026-ROOM1', 'Meeting Room B201', '4', 420000.00, '10001', '2', '1', 'af7a4c44-6f55-404f-b37f-efa673c76dab', '2026-01-05 09:30:00');
INSERT INTO `fixedassets` VALUES ('fa000001-0000-4000-8000-000000000105', 'FA-2026-004', 'Printer', '2', 3500.00, '10008', '2', '3', 'af7a4c44-6f55-404f-b37f-efa673c76dab', '2026-05-13 14:30:00');

-- Leave supplements (status: 0 pending, 1 approved, 2 rejected, 3 revoked)
INSERT INTO `leave1` VALUES ('lv000001-0000-4000-8000-000000000101', '2026-05-14 09:00:00', '2026-05-15 18:00:00', 'Family matters', '10003', '16', '2', '1', 'tsk-leave-approve-10003', '1', '2026-05-10 08:50:00');
INSERT INTO `leave1` VALUES ('lv000001-0000-4000-8000-000000000102', '2026-05-20 09:00:00', '2026-05-20 18:00:00', 'Medical check', '10004', '8', '5', '0', 'tsk-leave-pending-10004', '1', '2026-05-18 09:20:00');
INSERT INTO `leave1` VALUES ('lv000001-0000-4000-8000-000000000103', '2026-05-09 09:00:00', '2026-05-09 18:00:00', 'Personal trip', '10007', '8', '4', '2', 'tsk-leave-reject-10007', '1', '2026-05-07 10:40:00');
INSERT INTO `leave1` VALUES ('lv000001-0000-4000-8000-000000000104', '2026-05-22 09:00:00', '2026-05-22 18:00:00', 'Cancelled by user', '10008', '8', '3', '3', 'tsk-leave-cancel-10008', '1', '2026-05-21 09:10:00');

-- Task supplements for leave/fixed approval center
INSERT INTO `task` VALUES ('tsk-leave-approve-10003', NULL, 'Alice leave request', '1', '10003', '10002', '2026-05-10 08:50:00', '2026-05-10 12:05:00', '1', 'lv000001-0000-4000-8000-000000000101', '10002');
INSERT INTO `task` VALUES ('tsk-leave-pending-10004', NULL, 'Bob leave request', '1', '10004', NULL, '2026-05-18 09:20:00', NULL, '0', 'lv000001-0000-4000-8000-000000000102', '10002');
INSERT INTO `task` VALUES ('tsk-leave-reject-10007', NULL, 'Eva leave request', '1', '10007', '10002', '2026-05-07 10:40:00', '2026-05-07 15:10:00', '2', 'lv000001-0000-4000-8000-000000000103', '10002');
INSERT INTO `task` VALUES ('tsk-fixed-approve-10003', NULL, 'Alice fixed asset request', '2', '10003', '10002', '2026-05-07 10:15:00', '2026-05-07 14:20:00', '1', 'fa000001-0000-4000-8000-000000000101', '10002');
INSERT INTO `task` VALUES ('tsk-fixed-pending-10004', NULL, 'Bob fixed asset request', '2', '10004', NULL, '2026-05-10 11:20:00', NULL, '0', 'fa000001-0000-4000-8000-000000000102', '10002');
INSERT INTO `task` VALUES ('tsk-fixed-reject-10007', NULL, 'Eva fixed asset request', '2', '10007', '10002', '2026-05-11 16:05:00', '2026-05-11 18:30:00', '2', 'fa000001-0000-4000-8000-000000000103', '10002');
INSERT INTO `task` VALUES ('tsk-repair-approve-10003', NULL, 'Alice check repair request', '4', '10003', '10002', '2026-05-18 10:05:00', '2026-05-18 11:20:00', '1', 'cr000001-0000-4000-8000-000000000101', '10002');
INSERT INTO `task` VALUES ('tsk-repair-pending-10004', NULL, 'Bob check repair request', '4', '10004', NULL, '2026-05-19 18:30:00', NULL, '0', 'cr000001-0000-4000-8000-000000000102', '10002');
INSERT INTO `task` VALUES ('tsk-repair-reject-10007', NULL, 'Eva check repair request', '4', '10007', '10002', '2026-05-16 18:20:00', '2026-05-16 19:05:00', '2', 'cr000001-0000-4000-8000-000000000103', '10002');

-- Meeting and notice supplements (type: 1 meeting, 2 notice)
INSERT INTO `meeting` VALUES ('mt000001-0000-4000-8000-000000000101', 'Weekly Engineering Sync', NULL, 'fa000001-0000-4000-8000-000000000104', 'R&D Team', 'Alice', 'Sprint review and blockers', '1', '10001', '2026-05-12 09:00:00', NULL, NULL, '2026-05-13 14:00:00', '2026-05-13 15:00:00');
INSERT INTO `meeting` VALUES ('mt000001-0000-4000-8000-000000000102', 'HR Policy Update', NULL, 'fa000001-0000-4000-8000-000000000104', 'All Staff', 'Cathy', 'Policy update for Q2', '1', '10005', '2026-05-15 10:00:00', NULL, NULL, '2026-05-16 10:30:00', '2026-05-16 11:30:00');
INSERT INTO `meeting` VALUES ('nt000001-0000-4000-8000-000000000201', 'Labor Day Schedule', NULL, NULL, '', '', 'Office closed during Labor Day holiday', '2', '10001', '2026-04-25 08:30:00', '2026-05-01', '2026-05-05', NULL, NULL);
INSERT INTO `meeting` VALUES ('nt000001-0000-4000-8000-000000000202', 'Attendance Rule Reminder', NULL, NULL, '', '', 'Please complete check-in/check-out daily on time', '2', '10006', '2026-05-08 08:45:00', '2026-05-08', '2026-06-30', NULL, NULL);

-- Attendance supplements (for check page, salary page, and exception stats)
INSERT INTO `check1` VALUES ('ck000001-0000-4000-8000-000000000101', '10001', '2026-05-06', '', '2026-05-06 08:25:00', '2026-05-06 18:05:00', '正常', '正常');
INSERT INTO `check1` VALUES ('ck000001-0000-4000-8000-000000000102', '10001', '2026-05-07', '', '2026-05-07 08:36:00', '2026-05-07 18:00:00', '迟到', '正常');
INSERT INTO `check1` VALUES ('ck000001-0000-4000-8000-000000000103', '10002', '2026-05-06', '', '2026-05-06 08:20:00', '2026-05-06 17:20:00', '正常', '早退');
INSERT INTO `check1` VALUES ('ck000001-0000-4000-8000-000000000104', '10002', '2026-05-07', '', '2026-05-07 08:32:00', '2026-05-07 18:10:00', '迟到', '正常');
INSERT INTO `check1` VALUES ('ck000001-0000-4000-8000-000000000105', '10003', '2026-05-08', '', '2026-05-08 08:18:00', '2026-05-08 18:03:00', '正常', '正常');
INSERT INTO `check1` VALUES ('ck000001-0000-4000-8000-000000000106', '10004', '2026-05-08', '', '2026-05-08 08:45:00', '2026-05-08 17:10:00', '迟到', '早退');
INSERT INTO `check1` VALUES ('ck000001-0000-4000-8000-000000000107', '10005', '2026-05-08', '', '2026-05-08 08:26:00', '2026-05-08 17:58:00', '正常', '正常');
INSERT INTO `check1` VALUES ('ck000001-0000-4000-8000-000000000108', '10007', '2026-05-12', '', '2026-05-12 08:50:00', '2026-05-12 18:15:00', '迟到', '正常');
INSERT INTO `check1` VALUES ('ck000001-0000-4000-8000-000000000109', '10008', '2026-05-12', '', '2026-05-12 08:10:00', '2026-05-12 17:00:00', '正常', '早退');

-- Leave-day attendance rows using exact leave type names from leavetype table
INSERT INTO `check1` (`id`, `employeeID`, `date`, `remarks`) SELECT 'ck000001-0000-4000-8000-000000000201', '10003', '2026-05-14', `name` FROM `leavetype` WHERE `id`='2';
INSERT INTO `check1` (`id`, `employeeID`, `date`, `remarks`) SELECT 'ck000001-0000-4000-8000-000000000202', '10003', '2026-05-15', `name` FROM `leavetype` WHERE `id`='2';

-- Salary supplements for check/salary pages
INSERT INTO `salary` VALUES ('sl000001-0000-4000-8000-000000000101', 15000, 22, 20, 0, 14850.00, '10001', '2026-05', 1, 1);
INSERT INTO `salary` VALUES ('sl000001-0000-4000-8000-000000000102', 80000, 22, 19, 0, 68990.00, '10002', '2026-05', 2, 1);
INSERT INTO `salary` VALUES ('sl000001-0000-4000-8000-000000000103', 9000, 22, 18, 2, 7263.64, '10003', '2026-05', 1, 0);
INSERT INTO `salary` VALUES ('sl000001-0000-4000-8000-000000000104', 9000, 22, 17, 1, 6854.55, '10004', '2026-05', 1, 1);
INSERT INTO `salary` VALUES ('sl000001-0000-4000-8000-000000000105', 5000, 22, 20, 0, 4545.45, '10005', '2026-05', 1, 0);
INSERT INTO `salary` VALUES ('sl000001-0000-4000-8000-000000000106', 15000, 21, 21, 0, 15000.00, '10006', '2026-04', 0, 0);
INSERT INTO `salary` VALUES ('sl000001-0000-4000-8000-000000000107', 9000, 21, 16, 1, 6807.14, '10007', '2026-04', 2, 0);
INSERT INTO `salary` VALUES ('sl000001-0000-4000-8000-000000000108', 9000, 21, 17, 0, 7235.71, '10008', '2026-04', 1, 0);

-- Keep summary quantities consistent with inserted demo data
UPDATE `department` d SET d.quantity = (SELECT COUNT(*) FROM `employee` e WHERE e.departmentID = d.id);
UPDATE `position` p SET p.quantity = (SELECT COUNT(*) FROM `employee` e WHERE e.employeePositionID = p.id);
UPDATE `employeetype` t SET t.quantity = (SELECT COUNT(*) FROM `employee` e WHERE e.employeeType = t.id);
UPDATE `fixedassettype` ft SET ft.quantity = (SELECT COUNT(*) FROM `fixedassets` f WHERE f.typeID = ft.id AND f.status = '1');

