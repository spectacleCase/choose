package com.choose.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.user.pojos.User;
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
 * @since 2024/5/27 上午12:53
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<Map<String, Object>> getMonthUsers();

}
