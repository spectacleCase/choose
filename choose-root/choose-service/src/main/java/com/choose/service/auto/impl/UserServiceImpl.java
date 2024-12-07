package com.choose.service.auto.impl;


import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.annotation.SysLog;
import com.choose.config.UserLocalThread;
import com.choose.constant.CommonConstants;
import com.choose.constant.FileConstant;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import com.choose.jwt.JWTUtils;
import com.choose.mapper.UserMapper;
import com.choose.redis.utils.RedisUtils;
import com.choose.service.auto.UserService;
import com.choose.service.tag.TagAssociationService;
import com.choose.service.tag.TagService;
import com.choose.string.StringUtils;
import com.choose.tag.dto.TagAssociationDto;
import com.choose.tag.pojos.Tag;
import com.choose.tag.pojos.TagAssociation;
import com.choose.tag.vo.TagAssociationVo;
import com.choose.user.dto.LoginDto;
import com.choose.user.pojos.SessionKeyAndOpenId;
import com.choose.user.pojos.User;
import com.choose.user.pojos.UserInfo;
import com.choose.user.vo.UserInfoVo;
import com.choose.user.vo.UserVo;
import com.choose.weChat.WeChatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 上午1:15
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    @Value("${auth.redis-token.timeout}")
    private String tokenTimeout;

    @Value("${user.defImage}")
    private String defImage;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private TagAssociationService tagAssociationService;

    @Resource
    private TagService tagService;

    /**
     * 微信小程序登录
     */
    @Override
    @SysLog("微信小程序登录")
    public UserInfoVo login(@NotBlank LoginDto dto) {
        if (dto.getCode().isEmpty()) {
            return null;
        }
        UserInfoVo userInfoVo = new UserInfoVo();
        UserInfo userInfo = new UserInfo();
        SessionKeyAndOpenId weChatAccessTokenResponse = WeChatUtils.getWeChatAccessTokenResponse(appid, secret, dto.getCode());
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getOpenid, weChatAccessTokenResponse.getOpenId()));
        if (Objects.isNull(user)) {
            user = new User();
            user.setOpenid(weChatAccessTokenResponse.getOpenId());
            user.setSessionKey(weChatAccessTokenResponse.getSessionKey());
            user.setStatus(1);
            user.setAvatar(defImage);
            String nickName = CommonConstants.StrName.NICKNAME + StringUtils.generateRandomString();
            user.setNickname(nickName);
            user.setLastLogin(new Date());
            userMapper.insert(user);
            log.info("insert user into database:{}", user);
        } else {
            user.setLastLogin(new Date());
            userMapper.updateById(user);
            log.info("user login:{}", user);
        }
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setAvatar(FileConstant.COS_HOST + user.getAvatar());
        userInfo.setId(String.valueOf(user.getId()));
        String token = buildToken(user);
        userInfoVo.setUserInfo(userInfo);
        if (token.isEmpty()) {
            return null;
        }
        userInfoVo.setToken(token);
        // userInfoVo.setTokenTimeout(new Date() );
        Date expirationTime = new Date(new Date().getTime() + Long.parseLong(tokenTimeout));
        userInfoVo.setTokenTimeout(expirationTime);
        UserLocalThread.set(userInfo);
        return userInfoVo;
    }

    /**
     * 添加token到redis
     */
    public String buildToken(User user) {
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("userId", user.getId().toString());
        String token = JWTUtils.getToken(tokenMap);

        // 构建Redis key
        String tokenKey = CommonConstants.RedisKeyPrefix.USER_TOKEN_CACHE_KEY + user.getId();
        String userInfoKey = CommonConstants.RedisKeyPrefix.USER_INFO_TOKEN_CACHE_KEY + user.getId();
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        String userInfoJson = JSON.toJSONString(userInfo);
        if (userInfoJson.isEmpty()) {
            return null;
        }
        // 存储 到 Redis
        redisUtils.set(tokenKey, token, Long.parseLong(tokenTimeout));
        redisUtils.set(userInfoKey, userInfoJson, Long.parseLong(tokenTimeout));
        return token;
    }

    /**
     * 验证token是否快过期
     */
    @Override
    public Boolean tokenExpired(String token) {
        Boolean result = null;
        String tokenKey = CommonConstants.RedisKeyPrefix.USER_TOKEN_CACHE_KEY + token;
        if (!token.isEmpty() && redisUtils.get(tokenKey) != null) {
            long expire = redisUtils.getExpire(tokenKey);
        }
        return null;
    }

    @Override
    public String refresh(String token) {
        if (token.isEmpty()) {
            return null;
        }
        DecodedJWT decode = JWTUtils.decode(token);
        Claim id = decode.getClaim("userId");
        String tokenKey = CommonConstants.RedisKeyPrefix.USER_TOKEN_CACHE_KEY + id.asString();
        String userInfoKey = CommonConstants.RedisKeyPrefix.USER_TOKEN_CACHE_KEY + id.asString();
        String userInfo = (String) redisUtils.get(userInfoKey);
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("userId", id.asString());
        String newToken = JWTUtils.getToken(tokenMap);
        redisUtils.set(tokenKey, newToken, Long.parseLong(tokenTimeout));
        redisUtils.set(userInfoKey, userInfo, Long.parseLong(tokenTimeout));
        return newToken;
    }

    /**
     * 获取用户信息
     */
    @Override
    @SysLog("获取用户信息")
    public UserInfo getUser() {
        UserInfo user = UserLocalThread.getUser();
        if (Objects.nonNull(user)) {
            // UserVo userVo = new UserVo();
            // BeanUtils.copyProperties(user, userVo);
            user.setAvatar(FileConstant.COS_HOST + user.getAvatar());
            return user;
        }
        throw new RuntimeException("未登录");
    }

    /**
     * 修改用户信息
     */
    @Override
    @SysLog("修改用户信息")
    public UserInfo setUser(UserVo userVo) {
        UserInfo userInfo = UserLocalThread.getUser();
        if (Objects.nonNull(userInfo)) {
            User user = userMapper.selectById(userInfo.getId());
            if (Objects.nonNull(user)) {
                // 假设userVo已经被定义且初始化
                copyNonNullProperties(userVo, user);
                userMapper.updateById(user);
                BeanUtils.copyProperties(user, userVo);
                BeanUtils.copyProperties(user, userInfo);
                String userInfoToken = CommonConstants.RedisKeyPrefix.USER_INFO_TOKEN_CACHE_KEY + user.getId();
                String userInfoJson = JSON.toJSONString(userInfo);
                redisUtils.set(userInfoToken, userInfoJson, Long.parseLong(tokenTimeout));
                userInfo.setAvatar(FileConstant.COS_HOST + userVo.getAvatar());
                return userInfo;
            }
        }
        return null;
    }


    // 通用方法，复制非null且非空字符串的属性
    public static void copyNonNullProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }

        Method[] sourceMethods = source.getClass().getMethods();
        Method[] targetMethods = target.getClass().getMethods();

        for (Method sourceMethod : sourceMethods) {
            if (sourceMethod.getName().startsWith("get") && sourceMethod.getParameterCount() == 0) {
                try {
                    Object value = sourceMethod.invoke(source);
                    if (value != null && !value.toString().isEmpty()) {
                        String setterName = "set" + sourceMethod.getName().substring(3);
                        for (Method targetMethod : targetMethods) {
                            if (targetMethod.getName().equals(setterName) && targetMethod.getParameterCount() == 1 &&
                                    targetMethod.getParameterTypes()[0].isAssignableFrom(value.getClass())) {
                                targetMethod.invoke(target, value);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("copyNonNullProperties error:{}", e.getMessage());
                    throw new RuntimeException("copyNonNullProperties error:", e);
                }
            }
        }
    }

    /**
     * 新用户添加标签属性
     */
    @Override
    @SysLog("新用户添加标签属性")
    public void addendTag(TagAssociationDto vo) {
        UserInfo user = UserLocalThread.getUser();
        if (Objects.nonNull(user)) {
            ArrayList<TagAssociation> tagAssociations = new ArrayList<>();
            List<Tag> tags = tagService.listByIds(vo.getTagList());
            if (tags.size() < vo.getTagList().size()) {
                throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
            }
            vo.getTagList().forEach(item -> {
                TagAssociation tagAssociation = new TagAssociation();
                tagAssociation.setModelId(Long.valueOf(user.getId()));
                tagAssociation.setTagId(item);
                tagAssociations.add(tagAssociation);
            });
            tagAssociationService.saveBatch(tagAssociations);
            return;
        }
        throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
    }

    /**
     * 获取该用户的标签
     */
    @SysLog("获取该用户的标签")
    @Override
    public ArrayList<TagAssociationVo> getUserTag() {
        UserInfo user = UserLocalThread.getUser();
        if (Objects.nonNull(user)) {
            ArrayList<TagAssociationVo> tagAssociationVos = new ArrayList<>();
            List<TagAssociation> tagAssociationList = tagAssociationService.list(Wrappers.<TagAssociation>lambdaQuery()
                    .eq(TagAssociation::getModelId, user.getId()));
            List<Long> tagIdList = tagAssociationList.stream().map(TagAssociation::getTagId).toList();
            List<Tag> tags = tagService.listByIds(tagIdList);
            tags.forEach(item -> {
                TagAssociationVo tagAssociationVo = new TagAssociationVo();
                tagAssociationVo.setTagId(item.getId());
                tagAssociationVo.setTagName(item.getTag());
                tagAssociationVos.add(tagAssociationVo);
            });
            return tagAssociationVos;
        }
        throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
    }

    /**
     * 修改用户头像
     *
     * @param fileName - 头像地址
     */
    @Override
    public void updateAvatar(String fileName) {
        UserInfo userInfo = UserLocalThread.getUser();
        User user = new User();
        if (Objects.nonNull(userInfo) && !fileName.isEmpty()) {
            BeanUtils.copyProperties(userInfo,user);
            user.setAvatar(fileName);
            userMapper.updateById(user);
            return;
        }
        throw new CustomException(AppHttpCodeEnum.PARAM_REQUIRE);
    }

    /**
     * 用户二维码
     */
    @Override
    public String userORCode() {
        String id = UserLocalThread.getUser().getId();
        User user = userMapper.selectById(id);
        String code  = "";
        try {
            code = StringUtils.crateQRCodeImg("png",id, 400, 400,FileConstant.COS_HOST + user.getAvatar());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return code;
    }
}


