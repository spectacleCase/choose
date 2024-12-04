package com.choose.ranking.pojos;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/5 上午1:27
 */

@Data
@TableName("choose_ranking")
public class Ranking {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 实体id
     */
    private Long modelId;

    /**
     * 所属栏目id
     */
    private Long columnId;

    /**
     * 分数
     */
    private Double mark;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
