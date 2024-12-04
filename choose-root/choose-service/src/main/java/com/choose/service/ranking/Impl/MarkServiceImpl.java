package com.choose.service.ranking.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.annotation.SysLog;
import com.choose.config.UserLocalThread;
import com.choose.dishes.dto.MarkDto;
import com.choose.dishes.pojos.Dishes;
import com.choose.dishes.pojos.Mark;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import com.choose.mapper.DishesMapper;
import com.choose.mapper.MarkMapper;
import com.choose.service.ranking.MarkService;
import com.choose.user.pojos.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/7 上午1:49
 */
@Service
@Slf4j
public class MarkServiceImpl extends ServiceImpl<MarkMapper, Mark> implements MarkService {

    @Resource
    private DishesMapper dishesMapper;


    /**
     * 给菜品评分
     */
    @Override
    @SysLog("菜品评分")
    public Mark dishesMark(MarkDto dto) {
        UserInfo user = UserLocalThread.getUser();
        if (Objects.isNull(user)) {
            log.error("dishesMark user is null");
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }
        Dishes dishes = dishesMapper.selectOne(Wrappers.<Dishes>lambdaQuery()
                .eq(Dishes::getId, dto.getDishesId()));
        if (Objects.isNull(dishes)) {
            log.error("dishes is null id:{}", dto.getDishesId());
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        Mark mark = getOne(Wrappers.<Mark>lambdaQuery()
                .eq(Mark::getUserId, user.getId())
                .eq(Mark::getDishesId, dishes.getId()));
        if (Objects.isNull(mark)) {
            mark = new Mark();
            mark.setDishesId(dto.getDishesId());
            mark.setMark(dto.getMark());
            mark.setUserId(Long.valueOf(user.getId()));
            save(mark);
            return mark;
        }
        mark.setMark(dto.getMark());
        updateById(mark);
        return mark;
    }
}
