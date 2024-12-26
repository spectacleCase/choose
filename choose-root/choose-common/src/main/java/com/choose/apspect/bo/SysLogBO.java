package com.choose.apspect.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 桌角的眼镜
 */
@Data
public class SysLogBO implements Serializable {

    /**
     *  添加序列化版本号
     */
    @Serial
    private static final long serialVersionUID = 1L;

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