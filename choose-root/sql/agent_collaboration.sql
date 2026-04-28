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
    status        VARCHAR(16)  DEFAULT 'SUCCESS'     COMMENT 'SUCCESS / FAIL',
    error_message VARCHAR(1000) DEFAULT NULL,
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_delete     TINYINT(1)   DEFAULT 0,
    KEY idx_trace_id (trace_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent调用日志(供管理端监控页使用)';
