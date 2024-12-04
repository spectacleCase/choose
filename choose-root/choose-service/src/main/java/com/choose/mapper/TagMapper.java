package com.choose.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.tag.pojos.Tag;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/9 上午1:09
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 取出符合ids里面的全部标签
     */
    @MapKey("modelId")
    Map<Long, List<String>> extractTagsByModelIds(List<Long> ids);

    /**
     * 根据菜品id获取对应标签
     */
    List<String> getDishesIdTag(Long id);
}
