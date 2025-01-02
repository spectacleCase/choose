package com.choose.apspect.bo;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/9 下午2:07
 */
public class LogQueueConsumer {
    // 日志队列
    private static final LinkedBlockingQueue<SysLogBO> LOG_QUEUE = new LinkedBlockingQueue<>();

    /**
     * 生产日志数据
     */
    public static void produceLog(SysLogBO log) {
        try {
            LOG_QUEUE.put(log);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /**
     * 消费日志数据
     */
    public static SysLogBO consumeLog() {
        try {
            return LOG_QUEUE.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取队列大小
     */
    public static int getQueueSize() {
        return LOG_QUEUE.size();
    }
}