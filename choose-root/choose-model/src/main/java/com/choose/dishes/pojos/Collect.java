package com.choose.dishes.pojos;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;



/**
 * @author chen
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_collect")
public class Collect extends BasePo {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private Long userid;

}
