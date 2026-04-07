CREATE DATABASE  IF NOT EXISTS `scs_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `scs_db`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: scs_db
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `biz_appeal`
--

DROP TABLE IF EXISTS `biz_appeal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_appeal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申诉ID',
  `violation_id` bigint NOT NULL COMMENT '关联违规ID',
  `user_id` bigint NOT NULL COMMENT '提交人ID',
  `appeal_number` varchar(50) DEFAULT NULL COMMENT '申诉编号 AP2024...',
  `platform` varchar(20) DEFAULT 'web' COMMENT '来源: web, miniapp',
  `reason` varchar(50) DEFAULT NULL COMMENT '申诉原因类型',
  `description` text COMMENT '申诉描述',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `evidence_files` json DEFAULT NULL COMMENT '证据文件 (视频/图片)',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态: pending, approved, rejected',
  `reply_content` text COMMENT '管理员处理回复',
  `handler_id` bigint DEFAULT NULL COMMENT '处理管理员ID',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `appeal_number` (`appeal_number`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='申诉记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_appeal`
--

LOCK TABLES `biz_appeal` WRITE;
/*!40000 ALTER TABLE `biz_appeal` DISABLE KEYS */;
-- 示例数据已移除，请执行 backend/init_test_data.sql 初始化中文样例数据。
/*!40000 ALTER TABLE `biz_appeal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_fence`
--

DROP TABLE IF EXISTS `biz_fence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_fence` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `range_desc` varchar(255) DEFAULT NULL COMMENT '范围文字描述',
  `coordinates` text COMMENT '围栏坐标点集合 (JSON: [{lat,lng},...])',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='电子围栏配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_fence`
--

LOCK TABLES `biz_fence` WRITE;
/*!40000 ALTER TABLE `biz_fence` DISABLE KEYS */;
-- 示例数据已移除，请执行 backend/init_test_data.sql 初始化中文样例数据。
/*!40000 ALTER TABLE `biz_fence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_track`
--

DROP TABLE IF EXISTS `biz_track`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_track` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `date` date NOT NULL COMMENT '日期',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `distance` decimal(10,2) DEFAULT NULL COMMENT '里程(km)',
  `track_points` longtext COMMENT '轨迹点集合 (JSON大字段存储，包含 time, lat, lng, speed)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='每日轨迹主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_track`
--

LOCK TABLES `biz_track` WRITE;
/*!40000 ALTER TABLE `biz_track` DISABLE KEYS */;
-- 示例数据已移除，请执行 backend/init_test_data.sql 初始化中文样例数据。
/*!40000 ALTER TABLE `biz_track` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_violation`
--

DROP TABLE IF EXISTS `biz_violation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_violation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '违规ID',
  `user_id` bigint DEFAULT NULL COMMENT '关联用户ID (若能识别身份)',
  `user_name` varchar(50) DEFAULT NULL COMMENT '冗余用户姓名',
  `violation_time` datetime NOT NULL COMMENT '违规时间',
  `place` varchar(100) NOT NULL COMMENT '违规地点',
  `type` varchar(50) NOT NULL COMMENT '违规类型: 超速, 违停, 逆行, 闯红灯',
  `detail` text COMMENT '详细描述',
  `penalty` varchar(100) DEFAULT NULL COMMENT '处罚结果',
  `status` varchar(20) DEFAULT 'processed' COMMENT '状态: pending待处理, processed已处理',
  `evidence_images` json DEFAULT NULL COMMENT '证据图片URL数组 (JSON格式存储)',
  `can_appeal` tinyint DEFAULT '1' COMMENT '是否可申诉',
  `appeal_status` varchar(20) DEFAULT 'none' COMMENT '申诉状态: none, pending, approved, rejected',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='违规记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_violation`
--

LOCK TABLES `biz_violation` WRITE;
/*!40000 ALTER TABLE `biz_violation` DISABLE KEYS */;
-- 示例数据已移除，请执行 backend/init_test_data.sql 初始化中文样例数据。
/*!40000 ALTER TABLE `biz_violation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_admin`
--

DROP TABLE IF EXISTS `sys_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '管理员账号',
  `password` varchar(100) NOT NULL COMMENT '加密密码',
  `name` varchar(50) DEFAULT NULL COMMENT '管理员姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系方式',
  `role` varchar(20) DEFAULT 'admin' COMMENT '角色: admin, super_admin',
  `department` varchar(50) DEFAULT NULL COMMENT '所属部门(如保卫处)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_admin`
--

LOCK TABLES `sys_admin` WRITE;
/*!40000 ALTER TABLE `sys_admin` DISABLE KEYS */;
-- 示例数据已移除，请执行 backend/init_test_data.sql 初始化中文样例数据。
/*!40000 ALTER TABLE `sys_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名/学号',
  `password` varchar(100) NOT NULL COMMENT '加密后的密码',
  `name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `department` varchar(50) DEFAULT NULL COMMENT '所属学院/部门',
  `user_type` varchar(20) DEFAULT 'student' COMMENT '用户类型: student, teacher, staff',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态: active, disabled',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
-- 示例数据已移除，请执行 backend/init_test_data.sql 初始化中文样例数据。
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-08 17:17:33
