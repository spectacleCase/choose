package com.choose.service.agent.tag;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.choose.mapper.TagMapper;
import com.choose.service.agent.cache.TwoLevelCache;
import com.choose.service.agent.core.AgentLLMClient;
import com.choose.tag.pojos.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 美食社区智能推荐分享 (论文 5.2):
 * 用户在发布美食分享时,系统从 dishName + content 自动提取关键词,
 * 给出相关的餐厅标签跟话题推荐。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TagSuggestService {

    private final AgentLLMClient llmClient;
    private final TagMapper tagMapper;
    private final TwoLevelCache cache;

    private static final String NS = "tag.suggest";

    /**
     * 从一段文本中提取美食标签 / 话题 / 关键词。
     * 返回结构: {
     *   matchedTags: [{id, name}],   // 命中已有标签库的
     *   newTags:     [string],       // LLM 建议的新标签 (库里没有,前端展示为参考)
     *   topics:      [string],       // 话题建议
     *   keywords:    [string]        // 抽取的关键词,用于全文搜索
     * }
     */
    public JSONObject extract(String content) {
        if (content == null || content.isBlank()) {
            return emptyResult();
        }
        // 走二级缓存,相同文本短期内不重复调 LLM
        return cache.get(NS, normalize(content), JSONObject.class, k -> doExtract(content));
    }

    private JSONObject doExtract(String content) {
        // 拉所有可用标签作为候选集喂给 LLM
        List<Tag> all = tagMapper.selectList(null);
        Map<String, Long> nameToId = new HashMap<>();
        StringBuilder candidates = new StringBuilder();
        for (Tag t : all) {
            if (t.getTag() == null) continue;
            nameToId.put(t.getTag(), t.getId());
            if (candidates.length() > 0) candidates.append(",");
            candidates.append(t.getTag());
        }

        String sys = """
                你是美食内容编辑助手。给一段用户发布的美食分享文本,你需要:
                1. 从给定的"已有标签集合"里挑出最匹配的(只能选集合里的);
                2. 如果发现明显合适但集合里没有的标签,放进 newTags 里建议;
                3. 抽 3-5 个话题(适合作为社区讨论标签),如 #减脂餐 #粤菜推荐;
                4. 抽 3-8 个关键词,用于后续相关餐厅的全文搜索召回。
                严格输出 JSON: {"matchedTagNames":[],"newTags":[],"topics":[],"keywords":[]}
                """;
        String user = "已有标签集合: " + candidates + "\n\n用户发布文本: " + content;

        try {
            String raw = llmClient.chat(sys, user);
            JSONObject obj = parseJson(raw);
            if (obj == null) return emptyResult();

            JSONArray matched = new JSONArray();
            JSONArray names = obj.getJSONArray("matchedTagNames");
            if (names != null) {
                for (int i = 0; i < names.size(); i++) {
                    String n = names.getString(i);
                    if (n == null) continue;
                    Long id = nameToId.get(n);
                    if (id != null) {
                        JSONObject m = new JSONObject();
                        m.put("id", id);
                        m.put("name", n);
                        matched.add(m);
                    }
                }
            }

            JSONObject ret = new JSONObject();
            ret.put("matchedTags", matched);
            ret.put("newTags", nullSafe(obj.getJSONArray("newTags")));
            ret.put("topics", nullSafe(obj.getJSONArray("topics")));
            ret.put("keywords", nullSafe(obj.getJSONArray("keywords")));
            return ret;
        } catch (Exception e) {
            log.warn("tag suggest failed: {}", e.getMessage());
            return emptyResult();
        }
    }

    private JSONObject parseJson(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String s = raw.trim();
        int lb = s.indexOf('{');
        int rb = s.lastIndexOf('}');
        if (lb < 0 || rb <= lb) return null;
        try { return JSON.parseObject(s.substring(lb, rb + 1)); } catch (Exception e) { return null; }
    }

    private JSONObject emptyResult() {
        JSONObject o = new JSONObject();
        o.put("matchedTags", new JSONArray());
        o.put("newTags", new JSONArray());
        o.put("topics", new JSONArray());
        o.put("keywords", new JSONArray());
        return o;
    }

    private JSONArray nullSafe(JSONArray a) { return a == null ? new JSONArray() : a; }

    private String normalize(String s) {
        // 用于做缓存 key,避免空格大小写差异
        return s.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}
