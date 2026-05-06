package com.choose.controller.agent;

import com.choose.agent.dto.AgentRecommendDto;
import com.choose.agent.dto.TagSuggestDto;
import com.choose.agent.pojos.AgentCallLog;
import com.choose.agent.pojos.RecommendFeedback;
import com.choose.config.UserLocalThread;
import com.choose.service.agent.AgentRecommendService;
import com.choose.service.agent.feedback.RecommendFeedbackService;
import com.choose.service.agent.log.AgentCallLogService;
import com.choose.service.agent.tag.TagSuggestService;
import com.choose.user.pojos.UserInfo;
import com.choose.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 多Agent协作推荐入口。论文4/5章核心创新点的对外接口。
 */
@RestController
@RequestMapping("/choose/recommend/v1/agent")
@RequiredArgsConstructor
public class AgentRecommendController {

    private final AgentRecommendService agentRecommendService;
    private final AgentCallLogService logService;
    private final TagSuggestService tagSuggestService;
    private final RecommendFeedbackService feedbackService;

    /** 多Agent协作推荐 */
    @PostMapping("/recommend")
    public Result recommend(@Valid @RequestBody AgentRecommendDto dto) {
        return Result.ok(agentRecommendService.recommend(dto));
    }

    /** 按链路ID取出全部Agent调用记录(管理端Agent监控页) */
    @GetMapping("/trace/{traceId}")
    public Result trace(@PathVariable String traceId) {
        return Result.ok(agentRecommendService.getTrace(traceId));
    }

    /** 最近调用列表 */
    @GetMapping("/calls/recent")
    public Result recent(@RequestParam(defaultValue = "50") Integer limit) {
        List<AgentCallLog> rows = logService.listRecent(limit);
        return Result.ok(rows);
    }

    /**
     * 用户提交满意度评价 (论文 5.2 推荐结果可对评价反馈)。
     * Body: {traceId, rating(1-5), comment?}
     */
    @PostMapping("/feedback")
    public Result feedback(@RequestBody RecommendFeedback fb) {
        if (fb.getTraceId() == null || fb.getTraceId().isBlank()) {
            return Result.error("traceId 必填");
        }
        if (fb.getRating() == null || fb.getRating() < 1 || fb.getRating() > 5) {
            return Result.error("rating 必须在 1-5");
        }
        UserInfo u = UserLocalThread.getUser();
        if (u != null && u.getId() != null && (fb.getUserId() == null || fb.getUserId().isBlank())) {
            fb.setUserId(String.valueOf(u.getId()));
        }
        feedbackService.save(fb);
        return Result.ok();
    }

    /**
     * 美食社区智能标签提取 (论文 5.2):
     * 用户发布美食分享时,前端把 dishName/content 传过来,后端调 LLM 返回:
     *   - matchedTags: 命中已有标签库的 [{id, name}]
     *   - newTags: LLM 建议但库里还没有的标签名
     *   - topics:  社区话题建议
     *   - keywords: 全文关键词
     */
    @PostMapping("/extract-tags")
    public Result extractTags(@Valid @RequestBody TagSuggestDto dto) {
        return Result.ok(tagSuggestService.extract(dto.getContent()));
    }
}
