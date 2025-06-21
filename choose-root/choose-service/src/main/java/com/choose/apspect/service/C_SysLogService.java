package com.choose.apspect.service;


import com.choose.apspect.bo.LogQueueConsumer;
import com.choose.apspect.bo.SysLogBO;
import com.choose.common.DateUtils;
import com.choose.config.RabbitMQConfig;
import com.choose.constant.CommonConstants;
import com.choose.service.common.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;


/**
 * @author 桌角的眼镜
 */
@Slf4j
@Service
public class C_SysLogService {

    @Value("${system.rabbitmq-consumption-log}")
    private Boolean rCLog;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private SysLogService sysLogService;


    public boolean save(SysLogBO sysLogBO) {

        try {
            if(rCLog) {
                rabbitTemplate.convertAndSend(RabbitMQConfig.LOG_EXCHANGE, RabbitMQConfig.LOG_ROUTING_KEY, sysLogBO);
                return true;
            }
        } catch (Exception e) {

        }

        // 本地兜底消费
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String sysLog = "clientIp:%s, url:%s, userAgent:%s, requestType:%s, requestContent:%s, requestTime:%s, responseTime:%s, duration:%s ms, responseContent:%s, success:%s, remark:%s, createDate:%s"
                .formatted(
                        sysLogBO.getClientIp(),
                        sysLogBO.getUrl(),
                        sysLogBO.getUserAgent(),
                        sysLogBO.getRequestType(),
                        sysLogBO.getRequestContent(),
                        sdf.format((sysLogBO.getRequestTime())),
                        sdf.format(sysLogBO.getResponseTime()),
                        sysLogBO.getDuration(),
                        sysLogBO.getResponseContent(),
                        sysLogBO.getSuccess(),
                        sysLogBO.getRemark(),
                        sdf.format(sysLogBO.getCreateDate()));
        log.info(sysLog);

        // 推入日志队列
        LogQueueConsumer.produceLog(sysLogBO);

        return true;
    }


    public void asyncLogSave(Integer code,String logMessage,String logLevel) {
        CompletableFuture.supplyAsync(() -> {
            SysLogBO sysLogBO = new SysLogBO();
            sysLogBO.setDuration(0L);
            sysLogBO.setRemark("服务端异常");
            // 记录请求信息
            sysLogBO.setClientIp("");
            sysLogBO.setUrl("");
            sysLogBO.setUserAgent("");
            sysLogBO.setRequestType("");
            sysLogBO.setRequestTime(new Date());
            sysLogBO.setRequestContent("");

            // 记录响应信息
            sysLogBO.setResponseTime(new Date());
            sysLogBO.setResponseContent("");
            sysLogBO.setCreateDate(DateUtils.getNow());
            sysLogBO.setSuccess(code.toString());
            sysLogBO.setLogLevel(logLevel);
            sysLogBO.setResponseContent(logMessage);
            return this.save(sysLogBO);
        });
    }


}