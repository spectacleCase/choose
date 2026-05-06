-- ============================================================
-- 多Agent协作推荐功能 schema 增量
-- 对应论文第5/6章: 营养知识库 + Agent调用日志
-- ============================================================

DROP TABLE IF EXISTS choose_dish_nutrition;
CREATE TABLE choose_dish_nutrition (
    id           BIGINT       NOT NULL PRIMARY KEY  COMMENT '主键',
    dish_id      BIGINT       NOT NULL              COMMENT '菜品ID,关联choose_dishes.id',
    calorie      DOUBLE       DEFAULT NULL          COMMENT '热量(千卡)',
    protein      DOUBLE       DEFAULT NULL          COMMENT '蛋白质(g)',
    fat          DOUBLE       DEFAULT NULL          COMMENT '脂肪(g)',
    carbs        DOUBLE       DEFAULT NULL          COMMENT '碳水(g)',
    fiber        DOUBLE       DEFAULT NULL          COMMENT '膳食纤维(g)',
    sodium       DOUBLE       DEFAULT NULL          COMMENT '钠(mg)',
    health_tags  VARCHAR(500) DEFAULT '[]'          COMMENT '健康标签JSON数组',
    source       VARCHAR(32)  DEFAULT 'manual'      COMMENT 'manual / llm_estimate',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_delete    TINYINT(1)   DEFAULT 0,
    UNIQUE KEY uk_dish_id (dish_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品营养成分知识库';


DROP TABLE IF EXISTS choose_agent_call_log;
CREATE TABLE choose_agent_call_log (
    id            BIGINT       NOT NULL PRIMARY KEY,
    trace_id      VARCHAR(64)  NOT NULL              COMMENT '链路ID,一次推荐贯穿多个Agent',
    agent_name    VARCHAR(64)  NOT NULL              COMMENT 'Agent名称',
    user_id       VARCHAR(64)  DEFAULT NULL,
    input         TEXT         DEFAULT NULL          COMMENT '输入文本',
    output        TEXT         DEFAULT NULL          COMMENT '输出JSON',
    steps         TEXT         DEFAULT NULL          COMMENT 'ReAct步骤JSON数组',
    elapsed_ms    BIGINT       DEFAULT NULL          COMMENT '耗时ms',
    token_usage   BIGINT       DEFAULT NULL          COMMENT 'Token消耗(估算)',
    status        VARCHAR(16)  DEFAULT 'SUCCESS'     COMMENT 'SUCCESS / FAIL',
    error_message VARCHAR(1000) DEFAULT NULL,
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_delete     TINYINT(1)   DEFAULT 0,
    KEY idx_trace_id (trace_id),
    KEY idx_create_time (create_time),
    KEY idx_agent_status (agent_name, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent调用日志(供管理端监控页使用)';


-- ============== 健康标签独立管理 ==============
DROP TABLE IF EXISTS choose_health_tag;
CREATE TABLE choose_health_tag (
    id           BIGINT       NOT NULL PRIMARY KEY,
    name         VARCHAR(64)  NOT NULL              COMMENT '标签名',
    definition   VARCHAR(500) DEFAULT NULL          COMMENT 'SpEL 表达式,可用变量: calorie/protein/fat/carbs/fiber/sodium',
    color        VARCHAR(16)  DEFAULT '#10b981'     COMMENT '前端展示色',
    status       TINYINT      DEFAULT 1             COMMENT '1=启用 0=停用',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_delete    TINYINT(1)   DEFAULT 0,
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康标签管理';

INSERT INTO choose_health_tag(id, name, definition, color, status) VALUES
(1,'低脂',  'fat < 12',                                 '#10b981',1),
(2,'高蛋白','protein > 20',                             '#3b82f6',1),
(3,'低盐',  'sodium < 600',                             '#06b6d4',1),
(4,'减脂期','calorie < 400 && fat < 12 && protein > 15','#f59e0b',1),
(5,'高热量','calorie > 700',                            '#ef4444',1),
(6,'清淡',  'sodium < 500 && fat < 15',                 '#84cc16',1);


-- ============== 食材主数据 ==============
DROP TABLE IF EXISTS choose_ingredient;
CREATE TABLE choose_ingredient (
    id           BIGINT       NOT NULL PRIMARY KEY,
    name         VARCHAR(64)  NOT NULL              COMMENT '食材名称',
    category     VARCHAR(32)  DEFAULT NULL          COMMENT '类别',
    calorie      DOUBLE       DEFAULT NULL          COMMENT '每100g 热量 kcal',
    protein      DOUBLE       DEFAULT NULL,
    fat          DOUBLE       DEFAULT NULL,
    carbs        DOUBLE       DEFAULT NULL,
    fiber        DOUBLE       DEFAULT NULL,
    sodium       DOUBLE       DEFAULT NULL,
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_delete    TINYINT(1)   DEFAULT 0,
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食材主数据';

INSERT INTO choose_ingredient(id, name, category, calorie, protein, fat, carbs, fiber, sodium) VALUES
(1,'鸡胸肉','肉类',165,31,3.6,0,0,74),
(2,'牛肉','肉类',250,26,15,0,0,72),
(3,'糙米','主食',111,2.6,0.9,23,1.8,5),
(4,'西兰花','蔬菜',34,2.8,0.4,7,2.6,33),
(5,'橄榄油','调味',884,0,100,0,0,2),
(6,'豆腐','豆制品',76,8,4.8,1.9,0.4,7);


-- ============== 菜品-食材关联 ==============
DROP TABLE IF EXISTS choose_dish_ingredient;
CREATE TABLE choose_dish_ingredient (
    id            BIGINT       NOT NULL PRIMARY KEY,
    dish_id       BIGINT       NOT NULL              COMMENT '菜品ID',
    ingredient_id BIGINT       NOT NULL              COMMENT '食材ID',
    amount        DOUBLE       DEFAULT NULL          COMMENT '一份菜里该食材的分量(g)',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_delete     TINYINT(1)   DEFAULT 0,
    KEY idx_dish (dish_id),
    KEY idx_ingredient (ingredient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品-食材关联';


-- ============== 推荐满意度反馈 ==============
DROP TABLE IF EXISTS choose_recommend_feedback;
CREATE TABLE choose_recommend_feedback (
    id           BIGINT       NOT NULL PRIMARY KEY,
    trace_id     VARCHAR(64)  NOT NULL              COMMENT '关联链路ID',
    user_id      VARCHAR(64)  DEFAULT NULL,
    rating       TINYINT      NOT NULL              COMMENT '1-5 分',
    comment      VARCHAR(500) DEFAULT NULL,
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_delete    TINYINT(1)   DEFAULT 0,
    KEY idx_trace (trace_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐满意度反馈';
