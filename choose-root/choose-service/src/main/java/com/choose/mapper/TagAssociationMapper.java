package com.choose.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.tag.pojos.Tag;
import com.choose.tag.pojos.TagAssociation;
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
 * @since 2024/6/9 上午1:08
 */
@Mapper
public interface TagAssociationMapper extends BaseMapper<TagAssociation> {

    List<Tag> getTagsByModelId(@Param("modelId") long modelId);

    @Select("SELECT * FROM choose_tag_association WHERE model_id = #{modelId}")
    List<TagAssociation> selectByModelId(@Param("modelId") Long modelId);
}

