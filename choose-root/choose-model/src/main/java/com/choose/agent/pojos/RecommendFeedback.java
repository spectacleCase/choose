package com.choose.agent.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户对一次推荐的满意度反馈 (论文 5.2 推荐效果分析视图)。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_recommend_feedback")
public class RecommendFeedback extends BasePo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 关联的链路ID */
    private String traceId;

    private String userId;

    /** 1-5 分 */
    private Integer rating;

    /** 文字反馈,可空 */
    private String comment;
}
