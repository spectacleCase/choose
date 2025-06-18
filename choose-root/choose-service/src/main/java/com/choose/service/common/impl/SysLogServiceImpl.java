package com.choose.service.common.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.apspect.bo.LogQueueConsumer;
import com.choose.apspect.bo.SysLogBO;
import com.choose.common.SysLog;
import com.choose.config.RabbitMQConfig;
import com.choose.mapper.SysLogMapper;
import com.choose.service.common.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/9 下午2:37
 */
@Service
@Slf4j
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService  {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final Integer MAX_QUEUE_SIZE = 500;

    @Resource
    private ApplicationContext applicationContext;

    private final Queue<SysLogBO> logQueue = new ConcurrentLinkedQueue<>();

    // @RabbitListener(queues = RabbitMQConfig.LOG_QUEUE)
    public void receiveLogMessage(SysLogBO logMessage) {
        log.info("Received log message: {}", logMessage);
        logQueue.add(logMessage);
        if (logQueue.size() >= MAX_QUEUE_SIZE) {
            saveLogsToDatabase(logQueue);
            logQueue.clear(); // 清空队列
        }
    }

    private void saveLogsToDatabase(Queue<SysLogBO> logQueue) {
        List<SysLog> sysLogs = new ArrayList<>();
        for (SysLogBO logMessage : logQueue) {
            SysLog sysLog = new SysLog();
            BeanUtils.copyProperties(logMessage, sysLog);
            sysLogs.add(sysLog);
        }
        SysLogService proxy = applicationContext.getBean(SysLogService.class);
        proxy.saveBatch(sysLogs);
        log.info("批量处理完成，处理日志数量: {}", sysLogs.size());
    }

    @Override
    @PostConstruct
    public void writeLog() {
        executorService.submit(() -> {
            log.info("执行写入日志");
            while (true) {
                int num = LogQueueConsumer.getQueueSize();
                if (num > 500) {
                    log.info("队列大小超过 500，开始批量处理日志");
                    saveSysLog(num);
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        log.error("线程休眠被中断", e);
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            log.info("日志写入任务结束");
        });
    }

    // @Override
    @PreDestroy
    public void close() {
        log.info("Spring Boot 服务关闭...");
        log.info("写入全部日志...");
        // int num = LogQueueConsumer.getQueueSize();
        // saveSysLog(num);
        saveLogsToDatabase(logQueue);
        logQueue.clear(); // 清空队列
    }


    /**
     * 保存日志
     */
    public void saveSysLog(int num) {
        ArrayList<SysLog> sysLogs = new ArrayList<>(num - 1);
        for (int i = 0; i < num - 1; i++) {
            SysLogBO log = LogQueueConsumer.consumeLog();
            SysLog sysLog = new SysLog();
            BeanUtils.copyProperties(log, sysLog);
            sysLogs.add(sysLog);
        }
        try {
            SysLogService proxy = applicationContext.getBean(SysLogService.class);
            proxy.saveBatch(sysLogs);
            log.info("批量处理完成，处理日志数量: {}", sysLogs.size());
        } catch (Exception e) {
            log.error("批量处理日志失败", e);
        }
    }

    @Transactional
    @Override
    public boolean saveBatch(Collection<SysLog> entityList) {
        return super.saveBatch(entityList);
    }

    public void stop() {
        executorService.shutdown();
    }


}