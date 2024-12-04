package com.choose.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.recommoend.pojos.Recommend;
import com.choose.recommoend.vo.RecommendListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/3 下午1:50
 */
@Mapper
public interface RecommendMapper extends BaseMapper<Recommend> {

    /**
     * 获取推荐列表
     */
    List<RecommendListVo> getRecommendRecordList(@Param("userId") Long userId,
                                                 @Param("current") int current,
                                                 @Param("size") int size);


    List<Integer> getWeekRecommend();
}
