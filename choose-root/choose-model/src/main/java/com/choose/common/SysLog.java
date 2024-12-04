package com.choose.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/**
 * @author 桌角的眼镜
 */
@Data
@TableName("choose_sys_log")
public class SysLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 客户端ip
     */
    private String clientIp;

    /**
     * 请求路径
     */
    private String url;

    /**
     * 用户
     */
    private String userAgent;

    /**
     * 请求方法
     */
    private String requestType;

    /**
     * 请求内容
     */
    private String requestContent;

    /**
     * 请求时间
     */
    private Date requestTime;

    /**
     * 响应时间
     */
    private Date responseTime;

    /**
     * 持续时间
     */
    private Long duration;

    /**
     * 响应内容
     */
    private String responseContent;

    /**
     * 响应状态
     */
    private String success;


    /**
     * 备注
     */
    private String remark;

    /**
     * 创建日期
     */
    private Date createDate;
}