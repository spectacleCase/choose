package com.choose.service.tag.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.mapper.TagAssociationMapper;
import com.choose.service.tag.TagAssociationService;
import com.choose.tag.pojos.Tag;
import com.choose.tag.pojos.TagAssociation;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
@Service
public class TagAssociationServiceImpl extends ServiceImpl<TagAssociationMapper, TagAssociation> implements TagAssociationService {

    @Resource
    private TagAssociationMapper tagAssociationMapper;

    @Override
    public List<Tag> getTagsByModelId(Long modelId) {
        return tagAssociationMapper.getTagsByModelId(modelId);
    }
}
