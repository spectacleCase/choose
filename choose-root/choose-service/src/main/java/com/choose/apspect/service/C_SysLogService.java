package com.choose.apspect.service;


import com.choose.apspect.bo.SysLogBO;
import com.choose.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @author 桌角的眼镜
 */
@Slf4j
@Service
public class C_SysLogService {

    @Resource
    private RabbitTemplate rabbitTemplate;


    public boolean save(SysLogBO sysLogBO) {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //
        // String sysLog = "clientIp:%s, url:%s, userAgent:%s, requestType:%s, requestContent:%s, requestTime:%s, responseTime:%s, duration:%s ms, responseContent:%s, success:%s, remark:%s, createDate:%s"
        //         .formatted(
        //                 sysLogBO.getClientIp(),
        //                 sysLogBO.getUrl(),
        //                 sysLogBO.getUserAgent(),
        //                 sysLogBO.getRequestType(),
        //                 sysLogBO.getRequestContent(),
        //                 sdf.format((sysLogBO.getRequestTime())),
        //                 sdf.format(sysLogBO.getResponseTime()),
        //                 sysLogBO.getDuration(),
        //                 sysLogBO.getResponseContent(),
        //                 sysLogBO.getSuccess(),
        //                 sysLogBO.getRemark(),
        //                 sdf.format(sysLogBO.getCreateDate()));
        // log.info(sysLog);

        // 推入日志队列
        // LogQueueConsumer.produceLog(sysLogBO);
        rabbitTemplate.convertAndSend(RabbitMQConfig.LOG_EXCHANGE, RabbitMQConfig.LOG_ROUTING_KEY, sysLogBO);
        return true;
    }


}