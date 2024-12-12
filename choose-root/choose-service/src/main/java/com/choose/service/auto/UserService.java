package com.choose.service.auto;

import com.baomidou.mybatisplus.extension.service.IService;
import com.choose.tag.dto.TagAssociationDto;
import com.choose.tag.vo.TagAssociationVo;
import com.choose.user.dto.LoginDto;
import com.choose.user.pojos.User;
import com.choose.user.pojos.UserInfo;
import com.choose.user.vo.UserInfoVo;
import com.choose.user.vo.UserVo;

import java.util.ArrayList;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 上午1:12
 */
public interface UserService extends IService<User> {

    /**
     * 微信小程序登录
     */
    UserInfoVo login(LoginDto dto);

    /**
     * 验证token是否过期
     */
    Boolean tokenExpired(String token);

    /**
     * 返回新的token
     */
    public String refresh(String token);

    /**
     * 获取用户信息
     */
    UserInfo getUser(String id);

    /**
     * 修改用户信息
     */
    UserInfo setUser(UserVo userVo);

    /**
     * 新用户添加标签属性
     */
    void addendTag(TagAssociationDto vo);

    /**
     * 获取该用户的标签
     */
    ArrayList<TagAssociationVo> getUserTag();

    /**
     * 修改用户头像
     * @param fileName - 头像地址
     */
    void updateAvatar(String fileName);

    /**
     * 用户二维码
     */
    String userORCode();
}
