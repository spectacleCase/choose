package com.choose.tag.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/9 上午1:05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("choose_tag")
public class Tag extends BasePo {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 父标签id
     */
    private Long parentTagId;

    /**
     * 标签名称
     */
    private String tag;
}
