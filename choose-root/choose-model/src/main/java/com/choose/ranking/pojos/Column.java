package com.choose.ranking.pojos;

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
 * @since 2024/6/5 上午1:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("choose_column")
public class Column extends BasePo {

    /**
     * 栏目id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 栏目名称
     */
    private String columnName;

}
