package com.choose.agent.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 健康标签独立管理 (论文 5.2 知识库管理模块)。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_health_tag")
public class HealthTag extends BasePo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 标签名 如: 低脂 / 高蛋白 / 低盐 */
    private String name;

    /**
     * 判定规则 (自然语言或表达式),给 NutritionAgent 的 prompt 用。
     * 示例: "热量 < 400 kcal AND 脂肪 < 12g"
     */
    private String definition;

    /** 颜色,前端展示用 hex */
    private String color;

    /** 1=启用 0=停用 */
    private Integer status;
}
