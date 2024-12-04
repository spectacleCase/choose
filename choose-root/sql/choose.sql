/*
 Navicat Premium Dump SQL

 Source Server         : 本机MySQL
 Source Server Type    : MySQL
 Source Server Version : 50716 (5.7.16-log)
 Source Host           : localhost:3306
 Source Schema         : choose

 Target Server Type    : MySQL
 Target Server Version : 50716 (5.7.16-log)
 File Encoding         : 65001

 Date: 26/11/2024 16:52:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for choose_chat
-- ----------------------------
DROP TABLE IF EXISTS `choose_chat`;
CREATE TABLE `choose_chat`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '信息类型',
  `sender` int(11) NULL DEFAULT NULL COMMENT '发送方',
  `receiver` int(11) NULL DEFAULT NULL COMMENT '接受方',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '信息内容',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '聊天消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for choose_collect
-- ----------------------------
DROP TABLE IF EXISTS `choose_collect`;
CREATE TABLE `choose_collect`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `userid` bigint(20) NOT NULL,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `is_delete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1854724343957454850 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_collect_chilren
-- ----------------------------
DROP TABLE IF EXISTS `choose_collect_chilren`;
CREATE TABLE `choose_collect_chilren`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) NOT NULL,
  `userid` bigint(20) NOT NULL,
  `dishes_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `dishes_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `coordinate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `is_delete` tinyint(1) NULL DEFAULT 0,
  `dish_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_column
-- ----------------------------
DROP TABLE IF EXISTS `choose_column`;
CREATE TABLE `choose_column`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '栏目id',
  `column_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '栏目名称',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '逻辑删除(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '栏目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_comment_notifications
-- ----------------------------
DROP TABLE IF EXISTS `choose_comment_notifications`;
CREATE TABLE `choose_comment_notifications`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `comment_id` bigint(20) NULL DEFAULT NULL COMMENT '评论通知id',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '发送用户id',
  `sender_id` bigint(20) NULL DEFAULT NULL COMMENT '接受用户id',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '发送类型（1评论 2 系统）',
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '消息内容',
  `is_read` tinyint(1) NULL DEFAULT NULL COMMENT '是否已读（1读 0 未读）',
  `sender_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发送用户头像',
  `sender_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发送用户头像',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_delete` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '逻辑删除(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '消息通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_dishes
-- ----------------------------
DROP TABLE IF EXISTS `choose_dishes`;
CREATE TABLE `choose_dishes`  (
  `id` bigint(20) NOT NULL COMMENT '菜品id',
  `dishes_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜品名称',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片url',
  `column_id` bigint(20) NOT NULL COMMENT '栏目id',
  `shop` bigint(20) NOT NULL COMMENT '所属店铺id',
  `mark` float NULL DEFAULT NULL COMMENT '评分',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '逻辑删除(0存在 1删除)',
  `is_audit` tinyint(1) NULL DEFAULT 0 COMMENT '审核状态（-1审核失败，0未审核，1审核通过）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_mark
-- ----------------------------
DROP TABLE IF EXISTS `choose_mark`;
CREATE TABLE `choose_mark`  (
  `id` bigint(20) NOT NULL COMMENT '评分id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `dishes_id` bigint(20) NOT NULL COMMENT '菜品id',
  `mark` int(11) NULL DEFAULT NULL COMMENT '分数',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '逻辑删除(0存在 1删除)',
  `is_audit` tinyint(1) NULL DEFAULT 0 COMMENT '审核状态（-1审核失败，0未审核，1审核通过）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜品评分表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_ranking
-- ----------------------------
DROP TABLE IF EXISTS `choose_ranking`;
CREATE TABLE `choose_ranking`  (
  `id` bigint(20) NOT NULL COMMENT '排行id',
  `model_id` bigint(20) NULL DEFAULT NULL COMMENT '实体id',
  `column_id` bigint(20) NOT NULL COMMENT '所属栏目id',
  `mark` float NULL DEFAULT 0 COMMENT '分数',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '排行榜表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_recommend
-- ----------------------------
DROP TABLE IF EXISTS `choose_recommend`;
CREATE TABLE `choose_recommend`  (
  `id` bigint(20) NOT NULL COMMENT '推荐id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `dishes_id` bigint(20) NULL DEFAULT NULL COMMENT '菜品id',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ai描述',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '逻辑删除(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '推荐记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_review
-- ----------------------------
DROP TABLE IF EXISTS `choose_review`;
CREATE TABLE `choose_review`  (
  `id` bigint(20) NOT NULL COMMENT '评论id',
  `dishes_id` bigint(20) NOT NULL COMMENT '菜品id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `review` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评论内容',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '逻辑删除(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜品评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_search_history
-- ----------------------------
DROP TABLE IF EXISTS `choose_search_history`;
CREATE TABLE `choose_search_history`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '搜索词',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1859967689034543107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '搜索记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for choose_shop_comment
-- ----------------------------
DROP TABLE IF EXISTS `choose_shop_comment`;
CREATE TABLE `choose_shop_comment`  (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  `top_id` bigint(20) NULL DEFAULT NULL COMMENT '店铺id',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父评论id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '评论文本',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图片url',
  `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户头像',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_delete` tinyint(1) NULL DEFAULT NULL COMMENT '逻辑删除（1删除 0 未删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '店铺评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_shops
-- ----------------------------
DROP TABLE IF EXISTS `choose_shops`;
CREATE TABLE `choose_shops`  (
  `id` bigint(20) NOT NULL COMMENT '店铺id',
  `shop_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片url',
  `coordinate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '坐标',
  `mark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评分',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '逻辑删除(0存在 1删除)',
  `is_audit` tinyint(1) NOT NULL DEFAULT 0 COMMENT '审核状态(-1审核失败，0未审核 1审核通过)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '店铺表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_sys_log
-- ----------------------------
DROP TABLE IF EXISTS `choose_sys_log`;
CREATE TABLE `choose_sys_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志记录ID',
  `client_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端IP',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '请求路径',
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户代理',
  `request_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '请求方法',
  `request_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '请求内容',
  `request_time` datetime NOT NULL COMMENT '请求时间',
  `response_time` datetime NOT NULL COMMENT '响应时间',
  `duration` bigint(20) NOT NULL COMMENT '持续时间',
  `response_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '响应内容',
  `success` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '响应状态',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '备注',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1860585256145051651 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for choose_tag
-- ----------------------------
DROP TABLE IF EXISTS `choose_tag`;
CREATE TABLE `choose_tag`  (
  `id` bigint(20) NOT NULL COMMENT '标签id',
  `parent_tag_id` bigint(20) NULL DEFAULT NULL COMMENT '父标签id',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态\r\n警用：0\r\n启动：1',
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签名称',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '逻辑删除(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_tag_association
-- ----------------------------
DROP TABLE IF EXISTS `choose_tag_association`;
CREATE TABLE `choose_tag_association`  (
  `id` bigint(20) NOT NULL COMMENT '标签关联id',
  `model_id` bigint(20) NULL DEFAULT NULL COMMENT '实体id',
  `tag_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签id',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '逻辑删除(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for choose_user
-- ----------------------------
DROP TABLE IF EXISTS `choose_user`;
CREATE TABLE `choose_user`  (
  `id` bigint(20) NOT NULL COMMENT '用户id',
  `openid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '小程序openId',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `gender` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '2' COMMENT '用户性别(1男 2女 0未知)',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `session_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '用户状态(0封禁 1启用)',
  `last_login` datetime NULL DEFAULT NULL COMMENT '最近登录时间  ',
  `ban_start_time` datetime NULL DEFAULT NULL COMMENT '封禁开始时间',
  `ban_end_time` datetime NULL DEFAULT NULL COMMENT '封禁结束时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '逻辑删除(0存在 1删除)',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个性签名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `openid`(`openid`) USING BTREE COMMENT '唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Function structure for generate_snowflake_id
-- ----------------------------
DROP FUNCTION IF EXISTS `generate_snowflake_id`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `generate_snowflake_id`() RETURNS bigint(20)
BEGIN
    DECLARE epoch BIGINT;
    DECLARE worker_id_bits BIGINT;
    DECLARE datacenter_id_bits BIGINT;
    DECLARE sequence_bits BIGINT;
    DECLARE timestamp_left_shift BIGINT;
    DECLARE datacenter_id_shift BIGINT;
    DECLARE worker_id_shift BIGINT;
    DECLARE sequence_mask BIGINT;
    DECLARE last_timestamp BIGINT;
    DECLARE sequence BIGINT;
    DECLARE now_millis BIGINT;
    DECLARE id BIGINT;

    SET epoch = 1420070400000; -- 2015-01-01 00:00:00
    SET worker_id_bits = 5; -- 5 bits for worker ID
    SET datacenter_id_bits = 5; -- 5 bits for datacenter ID
    SET sequence_bits = 12; -- 12 bits for sequence number

    SET timestamp_left_shift = worker_id_bits + datacenter_id_bits + sequence_bits;
    SET datacenter_id_shift = worker_id_bits + sequence_bits;
    SET worker_id_shift = sequence_bits;
    SET sequence_mask = -1 ^ (-1 << sequence_bits);

    SET last_timestamp = -1;
    SET sequence = 0;

    SET now_millis = FLOOR((UNIX_TIMESTAMP() * 1000 - epoch) * (1 << timestamp_left_shift));

    IF (last_timestamp = now_millis) THEN
        SET sequence = (sequence + 1) & sequence_mask;
        IF (sequence = 0) THEN
            SET now_millis = wait_next_millis(last_timestamp);
        END IF;
    ELSE
        SET sequence = 0;
    END IF;

    SET last_timestamp = now_millis;

    SET id = ((now_millis - epoch) << timestamp_left_shift)
        | (1 << datacenter_id_shift) -- datacenter ID (replace with actual datacenter ID)
        | (1 << worker_id_shift) -- worker ID (replace with actual worker ID)
        | sequence;

    RETURN id;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for next_id
-- ----------------------------
DROP FUNCTION IF EXISTS `next_id`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `next_id`() RETURNS bigint(20)
BEGIN
    DECLARE epoch BIGINT;
    DECLARE worker_id_bits BIGINT;
    DECLARE datacenter_id_bits BIGINT;
    DECLARE sequence_bits BIGINT;
    DECLARE timestamp_left_shift BIGINT;
    DECLARE datacenter_id_shift BIGINT;
    DECLARE worker_id_shift BIGINT;
    DECLARE sequence_mask BIGINT;
    DECLARE last_timestamp BIGINT;
    DECLARE sequence BIGINT;
    DECLARE now_millis BIGINT;
    DECLARE id BIGINT;

    SET epoch = 1420070400000; -- 2015-01-01 00:00:00
    SET worker_id_bits = 5; -- 5 bits for worker ID
    SET datacenter_id_bits = 5; -- 5 bits for datacenter ID
    SET sequence_bits = 12; -- 12 bits for sequence number

    SET timestamp_left_shift = worker_id_bits + datacenter_id_bits + sequence_bits;
    SET datacenter_id_shift = worker_id_bits + sequence_bits;
    SET worker_id_shift = sequence_bits;
    SET sequence_mask = -1 ^ (-1 << sequence_bits);

    SET last_timestamp = -1;
    SET sequence = 0;

    SET now_millis = FLOOR((UNIX_TIMESTAMP() * 1000 - epoch) * (1 << timestamp_left_shift));

    IF (last_timestamp = now_millis) THEN
        SET sequence = (sequence + 1) & sequence_mask;
        IF (sequence = 0) THEN
            SET now_millis = wait_next_millis(last_timestamp);
        END IF;
    ELSE
        SET sequence = 0;
    END IF;

    SET last_timestamp = now_millis;

    SET id = ((now_millis - epoch) << timestamp_left_shift)
        | (1 << datacenter_id_shift) -- datacenter ID (replace with actual datacenter ID)
        | (1 << worker_id_shift) -- worker ID (replace with actual worker ID)
        | sequence;

    RETURN id;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for wait_next_millis
-- ----------------------------
DROP FUNCTION IF EXISTS `wait_next_millis`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `wait_next_millis`(last_timestamp BIGINT) RETURNS bigint(20)
BEGIN
    DECLARE timestamp BIGINT;

    SET timestamp = FLOOR(UNIX_TIMESTAMP() * 1000);
    WHILE (timestamp <= last_timestamp) DO
        SET timestamp = FLOOR(UNIX_TIMESTAMP() * 1000);
    END WHILE;

    RETURN timestamp;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
