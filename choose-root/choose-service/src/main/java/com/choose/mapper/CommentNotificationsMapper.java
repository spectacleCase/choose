package com.choose.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.comment.pojos.CommentNotifications;
import com.choose.comment.vo.UserCommentVo;
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
 * @since 2024/10/24 下午5:59
 */
@Mapper
public interface CommentNotificationsMapper extends BaseMapper<CommentNotifications> {


    List<UserCommentVo> selectUserCommentVoList(@Param("userId") Long userId,
                                                @Param("offset")int pageNum,
                                                @Param("size") int pageSize);



}
