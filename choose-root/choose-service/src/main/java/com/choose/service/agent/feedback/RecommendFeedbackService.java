package com.choose.service.agent.feedback;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.agent.pojos.RecommendFeedback;
import com.choose.mapper.RecommendFeedbackMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendFeedbackService extends ServiceImpl<RecommendFeedbackMapper, RecommendFeedback> {

    /**
     * 满意度分布 1-5 各多少条。
     */
    public Map<Integer, Long> ratingDistribution(Date from, Date to) {
        LambdaQueryWrapper<RecommendFeedback> qw = new LambdaQueryWrapper<>();
        if (from != null) qw.ge(RecommendFeedback::getCreateTime, from);
        if (to != null) qw.le(RecommendFeedback::getCreateTime, to);
        List<RecommendFeedback> rows = list(qw);
        Map<Integer, Long> dist = new HashMap<>();
        for (int i = 1; i <= 5; i++) dist.put(i, 0L);
        for (RecommendFeedback r : rows) {
            if (r.getRating() == null) continue;
            dist.merge(r.getRating(), 1L, Long::sum);
        }
        return dist;
    }
}
