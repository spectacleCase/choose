package com.choose.apspect.service;


import com.choose.apspect.bo.LogQueueConsumer;
import com.choose.apspect.bo.SysLogBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @author 桌角的眼镜
 */
@Slf4j
@Service
public class C_SysLogService {


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
        LogQueueConsumer.produceLog(sysLogBO);
        return true;
    }


}