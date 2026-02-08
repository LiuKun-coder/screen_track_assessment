-- ============================================
-- Screen Track Assessment 数据库完整建库脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS `screen_track` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `screen_track`;

-- ============================================
-- 1. 用户表 (biz_user)
-- ============================================
DROP TABLE IF EXISTS `biz_appeal`;
DROP TABLE IF EXISTS `biz_violation`;
DROP TABLE IF EXISTS `biz_track`;
DROP TABLE IF EXISTS `biz_screen`;
DROP TABLE IF EXISTS `biz_fence`;
DROP TABLE IF EXISTS `biz_user`;

CREATE TABLE `biz_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名/登录账号',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
    `role` VARCHAR(32) NOT NULL DEFAULT 'user' COMMENT '角色: admin-管理员, user-普通用户',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 电子围栏表 (biz_fence)
-- ============================================
CREATE TABLE `biz_fence` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(128) NOT NULL COMMENT '围栏名称',
    `type` VARCHAR(32) NOT NULL COMMENT '围栏类型: circle-圆形, polygon-多边形, rectangle-矩形',
    `center_lng` DOUBLE DEFAULT NULL COMMENT '中心点经度(圆形围栏)',
    `center_lat` DOUBLE DEFAULT NULL COMMENT '中心点纬度(圆形围栏)',
    `radius` DOUBLE DEFAULT NULL COMMENT '半径/米(圆形围栏)',
    `points` TEXT DEFAULT NULL COMMENT '围栏顶点坐标JSON(多边形/矩形)',
    `color` VARCHAR(32) DEFAULT '#FF0000' COMMENT '围栏颜色',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `user_id` BIGINT DEFAULT NULL COMMENT '所属用户ID',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电子围栏表';

-- ============================================
-- 3. 轨迹表 (biz_track)
-- ============================================
CREATE TABLE `biz_track` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `lng` DOUBLE NOT NULL COMMENT '经度',
    `lat` DOUBLE NOT NULL COMMENT '纬度',
    `altitude` DOUBLE DEFAULT NULL COMMENT '海拔',
    `speed` DOUBLE DEFAULT NULL COMMENT '速度',
    `direction` DOUBLE DEFAULT NULL COMMENT '方向/航向角',
    `accuracy` DOUBLE DEFAULT NULL COMMENT '精度',
    `location_time` DATETIME NOT NULL COMMENT '定位时间',
    `device_id` VARCHAR(128) DEFAULT NULL COMMENT '设备ID',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_location_time` (`location_time`),
    KEY `idx_user_location_time` (`user_id`, `location_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轨迹点表';

-- ============================================
-- 4. 大屏/屏幕统计表 (biz_screen)
-- ============================================
CREATE TABLE `biz_screen` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
    `total_users` INT DEFAULT 0 COMMENT '总用户数',
    `online_users` INT DEFAULT 0 COMMENT '在线用户数',
    `total_tracks` INT DEFAULT 0 COMMENT '总轨迹数',
    `total_fences` INT DEFAULT 0 COMMENT '总围栏数',
    `total_violations` INT DEFAULT 0 COMMENT '总违规数',
    `total_appeals` INT DEFAULT 0 COMMENT '总申诉数',
    `today_tracks` INT DEFAULT 0 COMMENT '今日轨迹数',
    `today_violations` INT DEFAULT 0 COMMENT '今日违规数',
    `stat_date` DATE DEFAULT NULL COMMENT '统计日期',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大屏统计表';

-- ============================================
-- 5. 违规记录表 (biz_violation)
-- ============================================
CREATE TABLE `biz_violation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `fence_id` BIGINT DEFAULT NULL COMMENT '围栏ID',
    `fence_name` VARCHAR(128) DEFAULT NULL COMMENT '围栏名称',
    `type` VARCHAR(64) DEFAULT NULL COMMENT '违规类型: enter-进入围栏, leave-离开围栏, overspeed-超速',
    `lng` DOUBLE DEFAULT NULL COMMENT '违规时经度',
    `lat` DOUBLE DEFAULT NULL COMMENT '违规时纬度',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '违规地点描述',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '违规描述',
    `status` VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '状态: pending-待处理, appealing-申诉中, resolved-已处理, rejected-已驳回',
    `violation_time` DATETIME NOT NULL COMMENT '违规时间',
    `track_id` BIGINT DEFAULT NULL COMMENT '关联轨迹点ID',
    `evidence` VARCHAR(512) DEFAULT NULL COMMENT '证据/截图URL',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_fence_id` (`fence_id`),
    KEY `idx_violation_time` (`violation_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违规记录表';

-- ============================================
-- 6. 申诉表 (biz_appeal)
-- ============================================
CREATE TABLE `biz_appeal` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `violation_id` BIGINT NOT NULL COMMENT '违规记录ID',
    `user_id` BIGINT NOT NULL COMMENT '申诉用户ID',
    `reason` VARCHAR(1024) NOT NULL COMMENT '申诉理由',
    `evidence` VARCHAR(512) DEFAULT NULL COMMENT '申诉证据/附件URL',
    `status` VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '状态: pending-待审核, approved-通过, rejected-驳回',
    `reply` VARCHAR(1024) DEFAULT NULL COMMENT '审核回复',
    `reviewer_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `review_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_violation_id` (`violation_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申诉表';

-- ============================================
-- 外键约束 (可选，根据需要启用)
-- ============================================
ALTER TABLE `biz_fence` ADD CONSTRAINT `fk_fence_user` FOREIGN KEY (`user_id`) REFERENCES `biz_user` (`id`) ON DELETE SET NULL;
ALTER TABLE `biz_track` ADD CONSTRAINT `fk_track_user` FOREIGN KEY (`user_id`) REFERENCES `biz_user` (`id`) ON DELETE CASCADE;
ALTER TABLE `biz_screen` ADD CONSTRAINT `fk_screen_user` FOREIGN KEY (`user_id`) REFERENCES `biz_user` (`id`) ON DELETE SET NULL;
ALTER TABLE `biz_violation` ADD CONSTRAINT `fk_violation_user` FOREIGN KEY (`user_id`) REFERENCES `biz_user` (`id`) ON DELETE CASCADE;
ALTER TABLE `biz_violation` ADD CONSTRAINT `fk_violation_fence` FOREIGN KEY (`fence_id`) REFERENCES `biz_fence` (`id`) ON DELETE SET NULL;
ALTER TABLE `biz_violation` ADD CONSTRAINT `fk_violation_track` FOREIGN KEY (`track_id`) REFERENCES `biz_track` (`id`) ON DELETE SET NULL;
ALTER TABLE `biz_appeal` ADD CONSTRAINT `fk_appeal_violation` FOREIGN KEY (`violation_id`) REFERENCES `biz_violation` (`id`) ON DELETE CASCADE;
ALTER TABLE `biz_appeal` ADD CONSTRAINT `fk_appeal_user` FOREIGN KEY (`user_id`) REFERENCES `biz_user` (`id`) ON DELETE CASCADE;
ALTER TABLE `biz_appeal` ADD CONSTRAINT `fk_appeal_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `biz_user` (`id`) ON DELETE SET NULL;

-- ============================================
-- 初始管理员账号 (密码需根据项目加密方式调整)
-- 默认密码: admin123 (如使用BCrypt请替换为加密后的值)
-- ============================================
INSERT INTO `biz_user` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PQm1z0iBMEMtKuz0VLaaG', '系统管理员', 'admin', 1);

-- ============================================
-- 完成
-- ============================================