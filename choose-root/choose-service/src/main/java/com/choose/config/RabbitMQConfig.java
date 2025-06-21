package com.choose.config;

import net.sf.jsqlparser.statement.select.Top;
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
    public static final String CRAWLER_QUEUE = "crawler-queue";
    public static final String COMMENT_NOTIF_QUEUE = "comment-notif";

    // 定义交换机名称
    public static final String LOG_EXCHANGE = "log-exchange";
    public static final String CRAWLER_EXCHANGE = "crawler-exchange";
    public static final String COMMENT_NOTIF_EXCHANGE = "comment-notif-exchange";

    // 定义路由键
    public static final String LOG_ROUTING_KEY = "log.routing.key";
    public static final String CRAWLER_ROUTING_KEY = "crawler.routing.key";
    public static final String COMMENT_NOTIF_KEY = "comment.notif.key";

    // 创建队列
    @Bean
    public Queue logQueue() {
        return new Queue(LOG_QUEUE, true);
    }

    @Bean
    public Queue crawlerQueue() {
        return new Queue(CRAWLER_QUEUE, true);
    }

    @Bean
    public Queue commentNotifQueue() {
        return new Queue(COMMENT_NOTIF_QUEUE, true);
    }

    // 创建交换机
    @Bean
    public TopicExchange logExchange() {
        return new TopicExchange(LOG_EXCHANGE);
    }

    @Bean
    public TopicExchange crawlerExchange() {
        return new TopicExchange(CRAWLER_EXCHANGE);
    }

    @Bean
    public TopicExchange commentNotifExchange() {
        return new TopicExchange(COMMENT_NOTIF_EXCHANGE);
    }


    // 绑定队列到交换机
    @Bean
    public Binding logBinding(Queue logQueue, TopicExchange logExchange) {
        return BindingBuilder.bind(logQueue).to(logExchange).with(LOG_ROUTING_KEY);
    }

    @Bean
    public Binding crawlerBinding(Queue crawlerQueue, TopicExchange crawlerExchange) {
        return BindingBuilder.bind(crawlerQueue).to(crawlerExchange).with(CRAWLER_ROUTING_KEY);
    }

    @Bean
    public Binding commentNotifBinding(Queue commentNotifQueue, TopicExchange commentNotifExchange) {
        return BindingBuilder.bind(commentNotifQueue).to(commentNotifExchange).with(COMMENT_NOTIF_KEY);
    }
}
