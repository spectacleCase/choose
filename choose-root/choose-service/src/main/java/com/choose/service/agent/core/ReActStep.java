package com.choose.service.agent.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * ReAct循环中的一条推理记录: Thought / Action / Observation / FinalAnswer
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReActStep {

    public enum Type { THOUGHT, ACTION, OBSERVATION, FINAL_ANSWER }

    private Type type;
    private String content;
    private long elapsedMs;
    private Instant timestamp;

    public static ReActStep of(Type type, String content, long elapsedMs) {
        return new ReActStep(type, content, elapsedMs, Instant.now());
    }
}
