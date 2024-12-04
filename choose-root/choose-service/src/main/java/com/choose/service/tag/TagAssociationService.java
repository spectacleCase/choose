package com.choose.service.tag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.choose.tag.pojos.Tag;
import com.choose.tag.pojos.TagAssociation;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/9 下午3:33
 */
public interface TagAssociationService extends IService<TagAssociation> {

    /**
     * 连表查询菜品标签
     */
    List<Tag> getTagsByModelId(Long modelId);
}
