package com.choose.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/17 01:15
 */


@Configuration
public class RabbitMQConfig {

    // 定义队列名称
    public static final String LOG_QUEUE = "log-queue";

    // 定义交换机名称
    public static final String LOG_EXCHANGE = "log-exchange";

    // 定义路由键
    public static final String LOG_ROUTING_KEY = "log.routing.key";



    // 创建队列
    @Bean
    public Queue logQueue() {
        return new Queue(LOG_QUEUE, true);
    }

    // 创建交换机
    @Bean
    public TopicExchange logExchange() {
        return new TopicExchange(LOG_EXCHANGE);
    }

    // 绑定队列到交换机
    @Bean
    public Binding logBinding(Queue logQueue, TopicExchange logExchange) {
        return BindingBuilder.bind(logQueue).to(logExchange).with(LOG_ROUTING_KEY);
    }
}
