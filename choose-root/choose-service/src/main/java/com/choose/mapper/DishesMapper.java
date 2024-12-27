package com.choose.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.admin.common.TodoItems;
import com.choose.common.SearchTerm;
import com.choose.dishes.pojos.Dishes;
import com.choose.recommoend.vo.RecommendVo;
import com.choose.search.vo.SearchVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/3 下午1:48
 */
@Mapper
public interface DishesMapper extends BaseMapper<Dishes> {

    /**
     * 获取坐标
     */
    String selectCoordinate(@Param("dishesId") Long dishesId);

    /**
     * 获取未审核的菜品和店铺
     */
    TodoItems getPendingList();

    /**
     * 推荐的菜品
     */
    List<RecommendVo> getRecommendVosByIds(List<String> dishIds);

    /**
     * 菜品中有关联的搜索词
     */
    List<SearchTerm> selectSearchTerm(@Param("keyword") String keyword);

    /**
     * 搜索结果
     */
    List<SearchVo> searchShopsAndDishes(@Param("searchTerm") String searchTerm);

    @Select("SELECT * FROM choose_dishes WHERE is_audit = 0 LIMIT #{offset}, #{pageSize}")
    List<Dishes> selectNotExaminedDishes(@Param("offset") int offset, @Param("pageSize") int pageSize);
}
