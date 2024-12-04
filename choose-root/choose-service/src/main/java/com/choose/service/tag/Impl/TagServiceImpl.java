package com.choose.service.tag.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.mapper.TagMapper;
import com.choose.service.tag.TagService;
import com.choose.tag.pojos.Tag;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/9 下午3:42
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
}
