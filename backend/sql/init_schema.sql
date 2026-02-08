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
INSERT INTO `biz_appeal` VALUES (1,2,2,'AP2026020001','web','Factual Error','My e-bike was parked in the designated area. The camera angle may have caused misjudgment.','13800138001','[\"https://example.com/appeal/evidence001.jpg\"]','pending',NULL,NULL,NULL,'2026-02-06 20:23:05'),(2,1,1,'AP2026020002','miniapp','Emergency','Had an emergency and needed to rush to the infirmary. Please consider the circumstances.','13900139002','[\"https://example.com/appeal/evidence002.jpg\"]','approved','Verified emergency situation. Penalty revoked. Please drive safely in the future.',1,'2026-02-06 20:23:05','2026-02-05 20:23:05'),(3,3,3,'AP2026020003','web','Insufficient Evidence','The surveillance footage is unclear. Cannot accurately identify if it was me or my vehicle.','13700137003','[]','rejected','Multi-angle surveillance confirmed identity. Appeal rejected.',1,'2026-02-07 14:23:05','2026-02-07 08:23:05');
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
INSERT INTO `biz_fence` VALUES (1,'Main Campus No-Go Zone','Library to Teaching Building A','[{\"lat\":30.5728,\"lng\":104.0668},{\"lat\":30.5735,\"lng\":104.0668},{\"lat\":30.5735,\"lng\":104.0678},{\"lat\":30.5728,\"lng\":104.0678}]','active','2026-02-07 20:23:05'),(2,'Student Dormitory Area','East Area Dorms 1-5','[{\"lat\":30.5750,\"lng\":104.0700},{\"lat\":30.5760,\"lng\":104.0700},{\"lat\":30.5760,\"lng\":104.0720},{\"lat\":30.5750,\"lng\":104.0720}]','active','2026-02-07 20:23:05'),(3,'Sports Field Area','Track and Basketball Courts','[{\"lat\":30.5700,\"lng\":104.0650},{\"lat\":30.5715,\"lng\":104.0650},{\"lat\":30.5715,\"lng\":104.0670},{\"lat\":30.5700,\"lng\":104.0670}]','active','2026-02-07 20:23:05'),(4,'Cafeteria Zone','Cafeteria 1 to Cafeteria 2','[{\"lat\":30.5740,\"lng\":104.0680},{\"lat\":30.5748,\"lng\":104.0680},{\"lat\":30.5748,\"lng\":104.0695},{\"lat\":30.5740,\"lng\":104.0695}]','inactive','2026-02-07 20:23:05'),(5,'主校区安全围栏',NULL,NULL,'active','2026-02-07 20:25:19');
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
INSERT INTO `biz_track` VALUES (1,1,'2026-02-07','2026-02-07 17:23:05','2026-02-07 18:23:05',3.50,'[{\"time\":\"2026-02-07T17:20:00\",\"lat\":30.5728,\"lng\":104.0668,\"speed\":15},{\"time\":\"2026-02-07T17:25:00\",\"lat\":30.5735,\"lng\":104.0675,\"speed\":18},{\"time\":\"2026-02-07T17:30:00\",\"lat\":30.5740,\"lng\":104.0680,\"speed\":12}]','2026-02-07 20:23:05'),(2,2,'2026-02-07','2026-02-07 15:23:05','2026-02-07 16:23:05',5.20,'[{\"time\":\"2026-02-07T15:20:00\",\"lat\":30.5700,\"lng\":104.0650,\"speed\":22},{\"time\":\"2026-02-07T15:30:00\",\"lat\":30.5720,\"lng\":104.0665,\"speed\":25},{\"time\":\"2026-02-07T15:40:00\",\"lat\":30.5735,\"lng\":104.0680,\"speed\":18}]','2026-02-07 20:23:05'),(3,1,'2026-02-06','2026-02-06 17:23:05','2026-02-06 18:23:05',2.80,'[{\"time\":\"2026-02-06T17:20:00\",\"lat\":30.5745,\"lng\":104.0685,\"speed\":10},{\"time\":\"2026-02-06T17:30:00\",\"lat\":30.5752,\"lng\":104.0692,\"speed\":15},{\"time\":\"2026-02-06T17:40:00\",\"lat\":30.5758,\"lng\":104.0700,\"speed\":18}]','2026-02-06 20:23:05'),(4,3,'2026-02-07','2026-02-07 19:23:05','2026-02-07 19:53:05',1.50,'[{\"time\":\"2026-02-07T19:20:00\",\"lat\":30.5730,\"lng\":104.0660,\"speed\":12},{\"time\":\"2026-02-07T19:30:00\",\"lat\":30.5738,\"lng\":104.0668,\"speed\":16},{\"time\":\"2026-02-07T19:40:00\",\"lat\":30.5745,\"lng\":104.0675,\"speed\":14}]','2026-02-07 20:23:05');
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
INSERT INTO `biz_violation` VALUES (1,1,'Zhang San','2026-02-05 20:23:05','Library Plaza','Speeding','E-bike speed exceeded 25km/h, measured at 32km/h','Warning','processed','[\"https://example.com/evidence/img001.jpg\"]',1,'none','2026-02-05 20:23:05',0),(2,2,'Li Si','2026-02-06 20:23:05','Teaching Building A Entrance','Illegal Parking','Parked in no-parking zone, blocking traffic','Warning','processed','[\"https://example.com/evidence/img002.jpg\"]',1,'pending','2026-02-06 20:23:05',0),(3,3,'Wang Wu','2026-02-07 08:23:05','Main Road','Wrong Way','Riding against traffic on main campus road','Verbal Warning','pending','[\"https://example.com/evidence/img004.jpg\"]',1,'none','2026-02-07 08:23:05',0),(4,1,'Zhang San','2026-02-07 14:23:05','Campus Gate','Red Light','Ignored traffic signal and ran red light','Demerit','pending','[\"https://example.com/evidence/img005.jpg\"]',1,'none','2026-02-07 14:23:05',0),(5,2,'Li Si','2026-02-07 17:23:05','Dormitory Area','Speeding','Speed exceeded limit in dorm area, measured 28km/h','Warning','pending','[\"https://example.com/evidence/img007.jpg\"]',0,'none','2026-02-07 17:23:05',0);
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
INSERT INTO `sys_admin` VALUES (1,'kunliu','123456','???',NULL,'admin','??????','2026-02-07 19:54:36',0);
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
INSERT INTO `sys_user` VALUES (1,'liukun','123456','liukun',NULL,NULL,NULL,NULL,'student','active','2026-01-31 20:39:20','2026-01-31 20:39:20',0);
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
