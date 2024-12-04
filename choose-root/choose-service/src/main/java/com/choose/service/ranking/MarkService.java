package com.choose.service.ranking;

import com.baomidou.mybatisplus.extension.service.IService;
import com.choose.dishes.dto.MarkDto;
import com.choose.dishes.pojos.Mark;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/7 上午1:49
 */
public interface MarkService extends IService<Mark> {

    /**
     * 给菜品评分
     */
    Mark dishesMark(MarkDto dto);
}
