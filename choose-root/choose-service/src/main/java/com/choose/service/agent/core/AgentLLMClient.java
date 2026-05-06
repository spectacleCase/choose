package com.choose.service.agent.core;

import com.choose.service.aiModel.DeepSeekV3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Deque;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Agent 专用 LLM 调用入口。封装 DeepSeekV3Service 并提供:
 *   1. 30s 超时(论文 5.1)
 *   2. 失败重试最多 3 次(论文 4.3)
 *   3. 简易熔断(论文 3.2): 60s 滑动窗口内失败率超过阈值时短路若干秒
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AgentLLMClient {

    private final DeepSeekV3Service<Object> deepSeekV3Service;

    private static final int MAX_RETRY = 3;
    private static final long CALL_TIMEOUT_MS = 30_000L;
    private static final long WINDOW_MS = 60_000L;          // 滑动窗口
    private static final int  WINDOW_MIN_SAMPLES = 5;       // 触发熔断的最小样本数
    private static final double FAIL_RATE_THRESHOLD = 0.6;  // 失败率阈值
    private static final long OPEN_DURATION_MS = 30_000L;   // 熔断开启持续时间

    private final ExecutorService callPool = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "agent-llm-" + System.nanoTime());
        t.setDaemon(true);
        return t;
    });

    private final Breaker breaker = new Breaker();

    /**
     * 同步对话。失败/超时/熔断时抛 RuntimeException。
     */
    public String chat(String systemPrompt, String userPrompt) {
        if (breaker.isOpen()) {
            throw new LLMUnavailableException("LLM 熔断中,跳过本次调用");
        }
        Throwable last = null;
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                String out = callOnce(systemPrompt, userPrompt);
                breaker.recordSuccess();
                return out;
            } catch (TimeoutException te) {
                breaker.recordFail();
                last = te;
                log.warn("LLM 调用超时 attempt={} ({}ms)", attempt, CALL_TIMEOUT_MS);
            } catch (Exception e) {
                breaker.recordFail();
                last = e;
                log.warn("LLM 调用失败 attempt={}: {}", attempt, e.getMessage());
                if (breaker.isOpen()) break;
            }
            // 指数退避: 200ms / 600ms
            try { Thread.sleep(200L * attempt); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); break; }
        }
        throw new RuntimeException("LLM 重试 " + MAX_RETRY + " 次后仍失败", last);
    }

    private String callOnce(String systemPrompt, String userPrompt) throws Exception {
        Future<String> f = callPool.submit(() -> {
            StringBuilder out = deepSeekV3Service.extracted(systemPrompt, userPrompt);
            if (out == null) throw new RuntimeException("LLM 返回空");
            return out.toString();
        });
        try {
            return f.get(CALL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            f.cancel(true);
            throw te;
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            if (cause instanceof Exception ex) throw ex;
            throw ee;
        }
    }

    /** 给运维/管理端用,可观察熔断状态 */
    public Breaker.Snapshot breakerStatus() {
        return breaker.snapshot();
    }

    @PreDestroy
    void shutdown() {
        callPool.shutdownNow();
    }

    /**
     * 自定义异常: 标识熔断打开,而非真实调用失败。
     */
    public static class LLMUnavailableException extends RuntimeException {
        public LLMUnavailableException(String msg) { super(msg); }
    }

    /**
     * 简易滑动窗口熔断器,无外部依赖。
     */
    static class Breaker {
        private final Deque<Long> failTimes = new ConcurrentLinkedDeque<>();
        private final Deque<Long> successTimes = new ConcurrentLinkedDeque<>();
        private final AtomicLong openUntil = new AtomicLong(0);

        void recordSuccess() {
            successTimes.addLast(System.currentTimeMillis());
            evict();
        }

        void recordFail() {
            failTimes.addLast(System.currentTimeMillis());
            evict();
            int fail = failTimes.size();
            int total = fail + successTimes.size();
            if (total >= WINDOW_MIN_SAMPLES && (double) fail / total >= FAIL_RATE_THRESHOLD) {
                openUntil.set(System.currentTimeMillis() + OPEN_DURATION_MS);
            }
        }

        boolean isOpen() {
            return System.currentTimeMillis() < openUntil.get();
        }

        private void evict() {
            long cutoff = System.currentTimeMillis() - WINDOW_MS;
            evictFrom(failTimes, cutoff);
            evictFrom(successTimes, cutoff);
        }

        private void evictFrom(Deque<Long> q, long cutoff) {
            while (!q.isEmpty() && q.peekFirst() != null && q.peekFirst() < cutoff) {
                q.pollFirst();
            }
        }

        Snapshot snapshot() {
            evict();
            return new Snapshot(
                    isOpen(),
                    successTimes.size(),
                    failTimes.size(),
                    Math.max(0, openUntil.get() - System.currentTimeMillis())
            );
        }

        public record Snapshot(boolean open, int success, int fail, long remainOpenMs) {}
    }
}
