package com.choose.ai.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import com.choose.common.ObjectNodeTypeHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.JdbcType;

/**
 * @author lizhentao
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "choose_ai_model", autoResultMap = true)
public class AiModel extends BasePo {

    /**
     * 模型类型:text,image,embedding,rerank
     */
    @TableField("type")
    private String type;

    /**
     * 模型名称
     */
    @TableField("name")
    private String name;

    /**
     * 模型标题(更易理解记忆的名称)
     */
    @TableField("title")
    private String title;

    /**
     * 模型所属平台
     */
    @TableField("platform")
    private String platform;

    /**
     * 模型配置
     */
    @TableField("setting")
    private String setting;

    /**
     * 说明
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否免费(true:免费,false:收费)
     */
    @TableField("is_free")
    private Boolean isFree;

    /**
     * 状态(1:正常使用,0:不可用)
     */
    @TableField("is_enable")
    private Boolean isEnable;

    /**
     * 上下文长度
     */
    @TableField("context_window")
    private Integer contextWindow;

    /**
     * 最大输入长度
     */
    @TableField("max_input_tokens")
    private Integer maxInputTokens;

    /**
     * 最大输出长度
     */
    @TableField("max_output_tokens")
    private Integer maxOutputTokens;

    /**
     * 输入类型
     */
    @TableField("input_types")
    private String inputTypes;

    /**
     * 属性
     */
    @TableField(value = "properties", jdbcType = JdbcType.JAVA_OBJECT, typeHandler = ObjectNodeTypeHandler.class)
    private ObjectNode properties;
}
