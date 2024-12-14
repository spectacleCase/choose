package com.choose.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.im.pojos.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/24 下午3:58
 */
@Mapper
public interface ChatMapper  extends BaseMapper<ChatMessage>{

    List<ChatMessage> getChatUserList(String userId);
}
