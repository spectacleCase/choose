package com.choose.service.agent.security;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Prompt 安全防护 (论文 2.1.3 三层防护中的输入侧):
 *   - SQL 注入特征过滤
 *   - Prompt 注入 / 越狱诱导识别 (例: "忽略以上指令" / "你现在扮演...")
 *   - XSS / 脚本注入特征
 *   - 越界主题 (色情 / 暴力 / 政治) 简单关键词拦截
 *
 * 三种处理动作:
 *   PASS      - 通过, 原样进入 LLM
 *   SANITIZE  - 脱敏后进入 LLM (轻量风险)
 *   BLOCK     - 直接拒绝, 不调用 LLM (高风险)
 */
@Component
@Slf4j
public class PromptGuard {

    public enum Verdict { PASS, SANITIZE, BLOCK }

    @Data
    public static class InspectResult {
        private Verdict verdict;
        /** 触发的规则名,审计用 */
        private List<String> hits = new ArrayList<>();
        /** 经过脱敏后可继续使用的文本; PASS 时等于原文; BLOCK 时为 null */
        private String sanitizedText;
        /** 给前端的友好拒绝文案 (BLOCK 时填充) */
        private String rejectReason;
    }

    // ---------- 规则集 ----------

    /** SQL 注入特征。命中视为 BLOCK (用户美食查询不会出现这些 token) */
    private static final List<Pattern> SQL_INJECTION = List.of(
            Pattern.compile("\\b(union\\s+select|drop\\s+table|delete\\s+from|update\\s+\\w+\\s+set|insert\\s+into)\\b",
                    Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?i)\\b(or|and)\\s+1\\s*=\\s*1\\b"),
            Pattern.compile("(?i)';\\s*--"),
            Pattern.compile("(?i)/\\*.*?\\*/", Pattern.DOTALL)
    );

    /** Prompt 注入 / 越狱诱导。命中视为 BLOCK */
    private static final List<Pattern> JAILBREAK = List.of(
            Pattern.compile("(?i)(ignore|disregard|forget)\\s+(all\\s+)?(previous|prior|above|earlier)\\s+(instructions|prompts|rules)"),
            Pattern.compile("忽略(以上|之前|先前|前面)的?(所有)?(指令|提示词|规则|约束)"),
            Pattern.compile("(?i)你(现在|从现在起)?(扮演|是)\\s*(?!Agent|意图|菜品|营养|地理|结果)"),
            Pattern.compile("(?i)(jailbreak|DAN\\s*mode|developer\\s+mode)"),
            Pattern.compile("(?i)(reveal|show|print|输出|泄露)\\s+(your\\s+)?(system\\s+prompt|prompt|系统提示词|指令)"),
            Pattern.compile("(?i)pretend\\s+(you|to\\s+be)")
    );

    /** XSS / 脚本注入。脱敏处理 (拿掉标签即可,不必 BLOCK,可能误伤含 < > 的菜名) */
    private static final List<Pattern> SCRIPT = List.of(
            Pattern.compile("(?i)<\\s*script[^>]*>"),
            Pattern.compile("(?i)javascript\\s*:"),
            Pattern.compile("(?i)on(error|click|load|focus|blur|mouseover)\\s*=")
    );

    /** 越界主题关键词。命中视为 BLOCK 并给文明拒绝文案 */
    private static final List<Pattern> OUT_OF_SCOPE = List.of(
            Pattern.compile("(?i)(炸弹|爆炸物|武器制作|毒品|制毒)"),
            Pattern.compile("(?i)(政治|党派|颜色革命|颠覆)"),
            Pattern.compile("(?i)(自杀|自残|轻生)"),
            Pattern.compile("(?i)(色情|约炮|裸聊|porn)")
    );

    /** 输入长度上限 (避免恶意大文本耗 token) */
    private static final int MAX_LEN = 1000;

    // ---------- 入口 ----------

    public InspectResult inspect(String input) {
        InspectResult r = new InspectResult();
        if (input == null) {
            r.setVerdict(Verdict.PASS);
            r.setSanitizedText("");
            return r;
        }
        String text = input.trim();
        if (text.length() > MAX_LEN) {
            text = text.substring(0, MAX_LEN);
            r.getHits().add("LEN_TRUNCATE");
        }

        // 1. 越界主题: 直接 BLOCK
        for (Pattern p : OUT_OF_SCOPE) {
            if (p.matcher(text).find()) {
                r.getHits().add("OUT_OF_SCOPE:" + p.pattern());
                r.setVerdict(Verdict.BLOCK);
                r.setRejectReason("您的请求涉及到本系统服务范围之外的内容,无法处理。本系统仅提供美食推荐相关服务。");
                log.warn("PromptGuard BLOCK out-of-scope: {}", abbrev(input));
                return r;
            }
        }

        // 2. SQL 注入: BLOCK
        for (Pattern p : SQL_INJECTION) {
            if (p.matcher(text).find()) {
                r.getHits().add("SQL_INJECTION:" + p.pattern());
                r.setVerdict(Verdict.BLOCK);
                r.setRejectReason("您的输入触发了安全检测,请使用自然语言描述您的美食需求。");
                log.warn("PromptGuard BLOCK SQL: {}", abbrev(input));
                return r;
            }
        }

        // 3. Prompt 越狱: BLOCK
        for (Pattern p : JAILBREAK) {
            if (p.matcher(text).find()) {
                r.getHits().add("JAILBREAK:" + p.pattern());
                r.setVerdict(Verdict.BLOCK);
                r.setRejectReason("检测到非美食推荐相关的指令尝试,已自动拒绝。请直接描述您想吃什么。");
                log.warn("PromptGuard BLOCK jailbreak: {}", abbrev(input));
                return r;
            }
        }

        // 4. XSS / 脚本: SANITIZE (剥离危险片段)
        boolean sanitized = false;
        for (Pattern p : SCRIPT) {
            if (p.matcher(text).find()) {
                text = p.matcher(text).replaceAll(" ");
                r.getHits().add("XSS:" + p.pattern());
                sanitized = true;
            }
        }

        r.setSanitizedText(text);
        r.setVerdict(sanitized ? Verdict.SANITIZE : Verdict.PASS);
        return r;
    }

    private static String abbrev(String s) {
        if (s == null) return "";
        return s.length() <= 80 ? s : s.substring(0, 80) + "...";
    }
}
