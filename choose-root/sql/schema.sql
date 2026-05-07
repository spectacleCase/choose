-- Auto-generated DDL from entity classes (choose-model)
-- Drops & recreates all tables. Schema only, no seed data.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `choose_agent_call_log`;
CREATE TABLE `choose_agent_call_log` (
  `id` BIGINT NOT NULL,
  `trace_id` VARCHAR(64) DEFAULT NULL,
  `agent_name` VARCHAR(64) DEFAULT NULL,
  `user_id` VARCHAR(64) DEFAULT NULL,
  `input` TEXT,
  `output` TEXT,
  `steps` LONGTEXT,
  `elapsed_ms` BIGINT DEFAULT NULL,
  `token_usage` BIGINT DEFAULT NULL,
  `status` VARCHAR(16) DEFAULT NULL,
  `error_message` VARCHAR(512) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_trace` (`trace_id`),
  KEY `idx_agent` (`agent_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_dish_ingredient`;
CREATE TABLE `choose_dish_ingredient` (
  `id` BIGINT NOT NULL,
  `dish_id` BIGINT DEFAULT NULL,
  `ingredient_id` BIGINT DEFAULT NULL,
  `amount` DOUBLE DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_dish` (`dish_id`),
  KEY `idx_ingredient` (`ingredient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_dish_nutrition`;
CREATE TABLE `choose_dish_nutrition` (
  `id` BIGINT NOT NULL,
  `dish_id` BIGINT DEFAULT NULL,
  `calorie` DOUBLE DEFAULT NULL,
  `protein` DOUBLE DEFAULT NULL,
  `fat` DOUBLE DEFAULT NULL,
  `carbs` DOUBLE DEFAULT NULL,
  `fiber` DOUBLE DEFAULT NULL,
  `sodium` DOUBLE DEFAULT NULL,
  `health_tags` VARCHAR(512) DEFAULT NULL,
  `source` VARCHAR(32) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_dish` (`dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_health_tag`;
CREATE TABLE `choose_health_tag` (
  `id` BIGINT NOT NULL,
  `name` VARCHAR(64) DEFAULT NULL,
  `definition` VARCHAR(512) DEFAULT NULL,
  `color` VARCHAR(16) DEFAULT NULL,
  `status` INT DEFAULT 1,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_ingredient`;
CREATE TABLE `choose_ingredient` (
  `id` BIGINT NOT NULL,
  `name` VARCHAR(64) DEFAULT NULL,
  `category` VARCHAR(32) DEFAULT NULL,
  `calorie` DOUBLE DEFAULT NULL,
  `protein` DOUBLE DEFAULT NULL,
  `fat` DOUBLE DEFAULT NULL,
  `carbs` DOUBLE DEFAULT NULL,
  `fiber` DOUBLE DEFAULT NULL,
  `sodium` DOUBLE DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_recommend_feedback`;
CREATE TABLE `choose_recommend_feedback` (
  `id` BIGINT NOT NULL,
  `trace_id` VARCHAR(64) DEFAULT NULL,
  `user_id` VARCHAR(64) DEFAULT NULL,
  `rating` INT DEFAULT NULL,
  `comment` VARCHAR(512) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_trace` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_tool_call_log`;
CREATE TABLE `choose_tool_call_log` (
  `id` BIGINT NOT NULL,
  `trace_id` VARCHAR(64) DEFAULT NULL,
  `agent_name` VARCHAR(64) DEFAULT NULL,
  `tool_name` VARCHAR(64) DEFAULT NULL,
  `params` TEXT,
  `observation` TEXT,
  `elapsed_ms` BIGINT DEFAULT NULL,
  `status` VARCHAR(16) DEFAULT NULL,
  `error_message` VARCHAR(512) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_trace` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_ai_model`;
CREATE TABLE `choose_ai_model` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(32) DEFAULT NULL,
  `name` VARCHAR(128) DEFAULT NULL,
  `title` VARCHAR(128) DEFAULT NULL,
  `platform` VARCHAR(64) DEFAULT NULL,
  `setting` TEXT,
  `remark` VARCHAR(512) DEFAULT NULL,
  `is_free` TINYINT(1) DEFAULT 0,
  `is_enable` TINYINT(1) DEFAULT 1,
  `context_window` INT DEFAULT NULL,
  `max_input_tokens` INT DEFAULT NULL,
  `max_output_tokens` INT DEFAULT NULL,
  `input_types` VARCHAR(128) DEFAULT NULL,
  `properties` JSON DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_comment_notifications`;
CREATE TABLE `choose_comment_notifications` (
  `id` BIGINT NOT NULL,
  `comment_id` BIGINT DEFAULT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `sender_id` BIGINT DEFAULT NULL,
  `type` INT DEFAULT NULL,
  `message` VARCHAR(512) DEFAULT NULL,
  `is_read` INT DEFAULT 0,
  `sender_name` VARCHAR(64) DEFAULT NULL,
  `sender_avatar` VARCHAR(255) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_shop_comment`;
CREATE TABLE `choose_shop_comment` (
  `id` BIGINT NOT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `top_id` BIGINT DEFAULT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `content` TEXT,
  `image_url` VARCHAR(512) DEFAULT NULL,
  `user_avatar` VARCHAR(255) DEFAULT NULL,
  `user_name` VARCHAR(64) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_top` (`top_id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_sys_log`;
CREATE TABLE `choose_sys_log` (
  `id` BIGINT NOT NULL,
  `client_ip` VARCHAR(64) DEFAULT NULL,
  `url` VARCHAR(512) DEFAULT NULL,
  `user_agent` VARCHAR(512) DEFAULT NULL,
  `request_type` VARCHAR(16) DEFAULT NULL,
  `request_content` TEXT,
  `request_time` DATETIME DEFAULT NULL,
  `response_time` DATETIME DEFAULT NULL,
  `duration` BIGINT DEFAULT NULL,
  `response_content` TEXT,
  `success` VARCHAR(16) DEFAULT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  `log_level` VARCHAR(16) DEFAULT NULL,
  `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_collect`;
CREATE TABLE `choose_collect` (
  `id` BIGINT NOT NULL,
  `name` VARCHAR(64) DEFAULT NULL,
  `userid` BIGINT DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_userid` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_collect_chilren`;
CREATE TABLE `choose_collect_chilren` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT DEFAULT NULL,
  `userid` BIGINT DEFAULT NULL,
  `dish_id` BIGINT DEFAULT NULL,
  `dishes_name` VARCHAR(128) DEFAULT NULL,
  `dishes_image` VARCHAR(512) DEFAULT NULL,
  `coordinate` VARCHAR(64) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_userid` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_dishes`;
CREATE TABLE `choose_dishes` (
  `id` BIGINT NOT NULL,
  `dishes_name` VARCHAR(128) DEFAULT NULL,
  `image` VARCHAR(512) DEFAULT NULL,
  `column_id` BIGINT DEFAULT NULL,
  `shop` BIGINT DEFAULT NULL,
  `mark` DOUBLE DEFAULT NULL,
  `is_audit` INT DEFAULT 0,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_column` (`column_id`),
  KEY `idx_shop` (`shop`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_foods_heat`;
CREATE TABLE `choose_foods_heat` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(128) DEFAULT NULL,
  `alias` VARCHAR(128) DEFAULT NULL,
  `calories` INT DEFAULT NULL,
  `proportion` INT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_mark`;
CREATE TABLE `choose_mark` (
  `id` BIGINT NOT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `dishes_id` BIGINT DEFAULT NULL,
  `mark` INT DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_dishes` (`dishes_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_shops`;
CREATE TABLE `choose_shops` (
  `id` BIGINT NOT NULL,
  `shop_name` VARCHAR(128) DEFAULT NULL,
  `image` VARCHAR(512) DEFAULT NULL,
  `coordinate` VARCHAR(64) DEFAULT NULL,
  `mark` VARCHAR(16) DEFAULT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `is_audit` INT DEFAULT 0,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_chat`;
CREATE TABLE `choose_chat` (
  `id` BIGINT NOT NULL,
  `type` INT DEFAULT 0,
  `sender` BIGINT DEFAULT NULL,
  `receiver` BIGINT DEFAULT NULL,
  `content` TEXT,
  `is_read` INT DEFAULT 0,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_sender` (`sender`),
  KEY `idx_receiver` (`receiver`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_friend`;
CREATE TABLE `choose_friend` (
  `id` BIGINT NOT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `friend_id` BIGINT DEFAULT NULL,
  `status` INT DEFAULT 0,
  `remark` VARCHAR(64) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_friend` (`friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_group`;
CREATE TABLE `choose_group` (
  `id` BIGINT NOT NULL,
  `name` VARCHAR(128) DEFAULT NULL,
  `owner_id` BIGINT DEFAULT NULL,
  `avatar` VARCHAR(512) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_owner` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_group_member`;
CREATE TABLE `choose_group_member` (
  `id` BIGINT NOT NULL,
  `group_id` BIGINT DEFAULT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `role` INT DEFAULT 0,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_group` (`group_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_column`;
CREATE TABLE `choose_column` (
  `id` INT NOT NULL,
  `column_name` VARCHAR(128) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_ranking`;
CREATE TABLE `choose_ranking` (
  `id` BIGINT NOT NULL,
  `model_id` BIGINT DEFAULT NULL,
  `column_id` BIGINT DEFAULT NULL,
  `mark` DOUBLE DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_column` (`column_id`),
  KEY `idx_model` (`model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_recommend`;
CREATE TABLE `choose_recommend` (
  `id` BIGINT NOT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `dishes_id` BIGINT DEFAULT NULL,
  `description` VARCHAR(512) DEFAULT NULL,
  `is_success` INT DEFAULT 0,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_dishes` (`dishes_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_search_history`;
CREATE TABLE `choose_search_history` (
  `id` BIGINT NOT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `keyword` VARCHAR(255) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_tag`;
CREATE TABLE `choose_tag` (
  `id` BIGINT NOT NULL,
  `parent_tag_id` BIGINT DEFAULT NULL,
  `tag` VARCHAR(64) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_tag_association`;
CREATE TABLE `choose_tag_association` (
  `id` BIGINT NOT NULL,
  `model_id` BIGINT DEFAULT NULL,
  `tag_id` BIGINT DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_model` (`model_id`),
  KEY `idx_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_review`;
CREATE TABLE `choose_review` (
  `id` BIGINT NOT NULL,
  `dishes_id` BIGINT DEFAULT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `review` TEXT,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_dishes` (`dishes_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `choose_user`;
CREATE TABLE `choose_user` (
  `id` BIGINT NOT NULL,
  `openid` VARCHAR(128) DEFAULT NULL,
  `avatar` VARCHAR(512) DEFAULT NULL,
  `nickname` VARCHAR(64) DEFAULT NULL,
  `gender` VARCHAR(8) DEFAULT NULL,
  `phone` VARCHAR(32) DEFAULT NULL,
  `session_key` VARCHAR(255) DEFAULT NULL,
  `status` INT DEFAULT 1,
  `last_login` DATETIME DEFAULT NULL,
  `ban_start_time` DATETIME DEFAULT NULL,
  `ban_end_time` DATETIME DEFAULT NULL,
  `description` VARCHAR(512) DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_delete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_openid` (`openid`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
