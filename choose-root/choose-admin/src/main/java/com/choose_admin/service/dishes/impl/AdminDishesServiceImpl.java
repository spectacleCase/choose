package com.choose_admin.service.dishes.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.choose.admin.dishes.UpdateStatusDto;
import com.choose.common.CommonUtils;
import com.choose.common.dto.CommentPageDto;
import com.choose.dishes.pojos.Dishes;
import com.choose.dishes.pojos.Shops;
import com.choose.dishes.vo.AdminDishesVo;
import com.choose.dishes.vo.AdminShopVo;
import com.choose.mapper.*;
import com.choose.tag.pojos.Tag;
import com.choose.tag.pojos.TagAssociation;
import com.choose.user.pojos.User;
import com.choose_admin.service.dishes.AdminDishesService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/17 00:21
 */
@Service
public class AdminDishesServiceImpl implements AdminDishesService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ShopsMapper shopsMapper;

    @Resource
    private DishesMapper dishesMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private TagAssociationMapper tagAssociationMapper;

    @Override
    public List<AdminShopVo> getNotExamineShop(CommentPageDto commentPageDto) {
        // 确保页码有效
        if (commentPageDto.getPage() == 0) {
            commentPageDto.setPage(1);
        }
        Page<Shops> page = new Page<>(commentPageDto.getPage(), 5);

// 查询未审核的店铺信息（分页查询）
        LambdaQueryWrapper<Shops> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Shops::getIsAudit, 0);
        Page<Shops> shopsList = shopsMapper.selectPage(page, queryWrapper);

        // 提取所有店铺的发布用户id
        List<Long> userIds = shopsList.getRecords().stream()
                .map(Shops::getUserId)
                .distinct() // 去重，避免重复查询
                .collect(Collectors.toList());

        // 根据用户id列表查询用户信息
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> userList = userMapper.selectBatchIds(userIds);
            // 将用户信息存入 Map，方便后续快速查找
            userList.forEach(user -> userMap.put(user.getId(), user));
        }

        // // 提取所有店铺的 tag_id
        // List<Long> shopIds = shopsList.stream()
        //         .map(Shops::getId)
        //         .collect(Collectors.toList());
        //
        // // 查询所有与店铺相关的标签关联信息
        // List<TagAssociation> tagAssociations = tagAssociationMapper.selectList(
        //         new LambdaQueryWrapper<TagAssociation>()
        //                 .in(TagAssociation::getModelId, shopIds)
        // );
        //
        // // 提取所有标签id
        // List<Long> tagIds = tagAssociations.stream()
        //         .map(TagAssociation::getTagId)
        //         .distinct() // 去重
        //         .collect(Collectors.toList());
        //
        // // 根据标签id列表查询标签信息
        // Map<Long, Tag> tagMap = new HashMap<>();
        // if (!tagIds.isEmpty()) {
        //     List<Tag> tagList = tagMapper.selectBatchIds(tagIds);
        //     // 将标签信息存入 Map，方便后续快速查找
        //     tagList.forEach(tag -> tagMap.put(tag.getId(), tag));
        // }

        // 创建返回结果列表
        List<AdminShopVo> resultList = new ArrayList<>();

        // 遍历店铺信息，封装到 AdminShopVo 对象中
        for (Shops shop : shopsList.getRecords()) {
            AdminShopVo vo = new AdminShopVo();
            vo.setId(shop.getId().toString());
            vo.setShopName(shop.getShopName());
            // vo.setLocation(shop.getCoordinate());
            vo.setCreateTime(shop.getCreateTime());


            // 解析坐标为经纬度
            if (shop.getCoordinate() != null) {
                String[] coordinates = shop.getCoordinate().split(",");
                if (coordinates.length == 2) {
                    vo.setLongitude(coordinates[1]);
                    vo.setLatitude(coordinates[0]);
                    vo.setLocation(CommonUtils.geocode(coordinates[1] + "," +  coordinates[0]));
                }
            }

            // 从 userMap 中获取用户信息
            User user = userMap.get(shop.getUserId());
            if (user != null) {
                vo.setUsername(user.getNickname());
            }

            // 图片列表（假设 image 字段是逗号分隔的图片 URL）
            if (shop.getImage() != null) {
                vo.setImageUrls(Arrays.asList(shop.getImage().split(",")));
            }

            // 查询店铺对应的标签
            // List<String> tagList = tagAssociations.stream()
            //         .filter(ta -> ta.getModelId().equals(shop.getId()))
            //         .map(TagAssociation::getTagId)
            //         .distinct()
            //         .map(tagMap::get)
            //         .filter(Objects::nonNull)
            //         .map(Tag::getTag)
            //         .collect(Collectors.toList());

            // vo.setTagList(tagList);

            // 添加到结果列表
            resultList.add(vo);
        }

        return resultList;
    }

    @Override
    public List<AdminDishesVo> getNotExamineDishes(CommentPageDto commentPageDto) {
        Page<Dishes> page = new Page<>((commentPageDto.getPage()), 5);
        System.out.println(commentPageDto.getPage());

        LambdaQueryWrapper<Dishes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dishes::getIsAudit, 0);

        List<Dishes> dishesList = dishesMapper.selectPage(page,queryWrapper).getRecords();
        Set<Long> shopIds = dishesList.stream().map(Dishes::getShop).collect(Collectors.toSet());
        Set<Long> dishIds = dishesList.stream().map(Dishes::getId).collect(Collectors.toSet());
        if (shopIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Shops> shopsQuery = new LambdaQueryWrapper<>();
        shopsQuery.in(Shops::getId, shopIds);
        Map<Long, Shops> shopMap = shopsMapper.selectList(shopsQuery).stream()
                .collect(Collectors.toMap(Shops::getId, Function.identity()));
        if (dishIds.isEmpty()) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<TagAssociation> tagAssociationQuery = new LambdaQueryWrapper<>();
        tagAssociationQuery.in(TagAssociation::getModelId, dishIds);
        List<TagAssociation> tagAssociations = tagAssociationMapper.selectList(tagAssociationQuery);
        Set<Long> tagIds = tagAssociations.stream().map(TagAssociation::getTagId).collect(Collectors.toSet());
        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<Tag> tagQuery = new LambdaQueryWrapper<>();
        tagQuery.in(Tag::getId, tagIds);
        Map<Long, Tag> tagMap = tagMapper.selectList(tagQuery).stream()
                .collect(Collectors.toMap(Tag::getId, Function.identity()));

        Map<Long, List<TagAssociation>> dishTagMap = tagAssociations.stream()
                .collect(Collectors.groupingBy(TagAssociation::getModelId));
        List<AdminDishesVo> result = new ArrayList<>();
        for (Dishes dish : dishesList) {
            AdminDishesVo vo = new AdminDishesVo();
            vo.setId(dish.getId().toString());
            vo.setFoodName(dish.getDishesName());
            vo.setImageUrl(Arrays.asList(dish.getImage().split(",")));

            Shops shop = shopMap.get(dish.getShop());
            if (shop != null) {
                vo.setShopName(shop.getShopName());
                // vo.setLocation(shop.getCoordinate());
                // 解析坐标
                String[] coordinates = shop.getCoordinate().split(",");
                if (coordinates.length == 2) {
                    vo.setLongitude(coordinates[0]);
                    vo.setLatitude(coordinates[1]);
                    vo.setLocation(CommonUtils.geocode(coordinates[0] + "," +  coordinates[1]));
                }
            }

            List<TagAssociation> associations = dishTagMap.get(dish.getId());
            List<String> tags = new ArrayList<>();
            if (associations != null) {
                for (TagAssociation association : associations) {
                    Tag tag = tagMap.get(association.getTagId());
                    if (tag != null) {
                        tags.add(tag.getTag());
                    }
                }
            }
            vo.setTags(tags);
            vo.setSubmitTime(dish.getCreateTime());
            result.add(vo);
        }
        return result;
    }

    @Override
    public boolean updateShopStatus(UpdateStatusDto dto) {
        // 创建更新条件
        LambdaUpdateWrapper<Shops> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Shops::getId, dto.getId())
                .set(Shops::getIsAudit, dto.getStatus());

        // 执行更新操作
        int result = shopsMapper.update(null, updateWrapper);

        // 返回更新结果
        return result > 0;

    }

    @Override
    public boolean updateDishesStatus(UpdateStatusDto dto) {
        // 创建更新条件
        LambdaUpdateWrapper<Dishes> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Dishes::getId, dto.getId())
                .set(Dishes::getIsAudit, dto.getStatus());

        // 执行更新操作
        int result = dishesMapper.update(null, updateWrapper);

        // 返回更新结果
        return result > 0;
    }
}