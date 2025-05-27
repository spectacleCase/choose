package com.choose.service.recommend.Impl;

import com.choose.dishes.pojos.Dishes;
import com.choose.dishes.pojos.Shops;
import com.choose.recommoend.pojos.Recommend;
import com.choose.recommoend.vo.RecommendVo;
import com.choose.service.recommend.RecommendationStrategy;
import com.choose.tag.pojos.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * 随机推荐 - todo 待优化
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/14 下午10:00
 */
@Slf4j
@Service
public class RandomRecommend extends RecommendationStrategy {

    @Override
    public List<RecommendVo> recommendItems(String targetUserId, int k) {
        ArrayList<RecommendVo> recommendVos = new ArrayList<>();
        List<Dishes> dishesList = super.dishesMapper.selectList(null);
        for (int i = 0; i < k; i++) {
            int randomNumber = new Random().nextInt(dishesList.size()) + 1;
            log.info("随机数: {}", randomNumber);
            Recommend recommend = new Recommend();
            recommend.setUserId(Long.valueOf(targetUserId));
            recommend.setDishesId(dishesList.get(randomNumber).getId());
            recommend.setDescription("好吃");
            recommendMapper.insert(recommend);
            RecommendVo recommendVo = new RecommendVo();
            Dishes dishes = dishesList.get(randomNumber);
            BeanUtils.copyProperties(dishes, recommendVo);
            BeanUtils.copyProperties(recommend, recommendVo);
            recommendVo.setId(String.valueOf(recommend.getId()));
            recommendVo.setShopId(String.valueOf(dishes.getShop()));
            Shops shops = shopsMapper.selectById(dishes.getShop());
            recommendVo.setShopName(shops.getShopName());
            List<Tag> tagsByModelId = tagAssociationMapper.getTagsByModelId(shops.getId());
            ArrayList<String> objects = new ArrayList<>();
            for (Tag tag : tagsByModelId) {
                objects.add(tag.getTag());
            }
            recommendVo.setTagName(objects);
            recommendVo.setAiDescription(recommend.getDescription());
            recommendVo.setCoordinate(shops.getCoordinate());
            recommendVo.setTagName(objects);
            recommendVos.add(recommendVo);
        }
        return recommendVos;
    }
}
