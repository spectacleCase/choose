package com.choose.ranking.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/5 上午1:25
 */
@Data
public class ColumnVo {

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
