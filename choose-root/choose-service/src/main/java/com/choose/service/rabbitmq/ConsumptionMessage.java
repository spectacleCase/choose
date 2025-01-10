package com.choose.service.rabbitmq;

import com.choose.apspect.bo.SysLogBO;
import com.choose.config.RabbitMQConfig;
import com.choose.service.common.impl.FoodCrawlerService;
import com.choose.service.common.impl.SysLogServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/1/10 23:04
 */
@Component
public class ConsumptionMessage {

    @Resource
    private SysLogServiceImpl sysLogServiceImpl;

    @Resource
    private FoodCrawlerService foodCrawlerService;

    /**
     * 消费日志消息
     */
    @RabbitListener(queues = RabbitMQConfig.LOG_QUEUE)
    public void receiveLogMessage(SysLogBO logMessage) {
        sysLogServiceImpl.receiveLogMessage(logMessage);
    }

    /**
     * 消费爬虫任务
     */
    @RabbitListener(queues = RabbitMQConfig.CRAWLER_QUEUE)
    public void receiveLogMessage(String foodName) {
        System.out.println("开始消费" + foodName);
        foodCrawlerService.fetchFoodData(foodName);
    }
}
