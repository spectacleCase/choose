package com.choose.service.agent.tool;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.choose.dishes.pojos.Dishes;
import com.choose.dishes.pojos.Shops;
import com.choose.mapper.DishesMapper;
import com.choose.mapper.ShopsMapper;
import com.choose.mapper.TagMapper;
import com.choose.recommoend.vo.RecommendVo;
import com.choose.service.agent.core.AgentContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库菜品检索工具。基于关键词、菜系、口味等条件查询菜品。
 * 写入 ctx.candidates 供下游Agent复用。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DishQueryTool implements Tool {

    private final DishesMapper dishesMapper;
    private final ShopsMapper shopsMapper;
    private final TagMapper tagMapper;

    @Override
    public String name() { return "dish_query"; }

    @Override
    public String description() {
        return "基于关键词模糊查询菜品。可指定keyword字符串和limit数量。";
    }

    @Override
    public String parameterSchema() {
        return "{\"keyword\":\"模糊匹配菜名/店名\", \"limit\":返回上限(默认20)}";
    }

    @Override
    public String execute(JSONObject params, AgentContext ctx) {
        String keyword = params.getString("keyword");
        Integer limit = params.getInteger("limit");
        if (limit == null || limit <= 0) limit = 20;

        LambdaQueryWrapper<Dishes> qw = new LambdaQueryWrapper<>();
        qw.eq(Dishes::getIsAudit, 1);
        if (keyword != null && !keyword.isBlank()) {
            qw.like(Dishes::getDishesName, keyword);
        }
        qw.last("LIMIT " + limit);

        List<Dishes> rows = dishesMapper.selectList(qw);
        List<RecommendVo> vos = new ArrayList<>();
        for (Dishes d : rows) {
            RecommendVo v = new RecommendVo();
            v.setId(String.valueOf(d.getId()));
            v.setDishesName(d.getDishesName());
            v.setImage(d.getImage());
            v.setMark(d.getMark());
            if (d.getShop() != null) {
                v.setShopId(String.valueOf(d.getShop()));
                Shops shop = shopsMapper.selectById(d.getShop());
                if (shop != null) {
                    v.setShopName(shop.getShopName());
                    v.setCoordinate(shop.getCoordinate());
                }
            }
            try {
                v.setTagName(tagMapper.getDishesIdTag(d.getId()));
            } catch (Exception ignored) {}
            vos.add(v);
        }
        ctx.getCandidates().clear();
        ctx.getCandidates().addAll(vos);
        return "命中候选 " + vos.size() + " 条; ids=" + ids(vos);
    }

    private String ids(List<RecommendVo> vos) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vos.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(vos.get(i).getId());
        }
        return sb.append("]").toString();
    }
}
