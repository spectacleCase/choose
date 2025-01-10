package com.choose.service.dishes.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.annotation.SysLog;
import com.choose.common.dto.CommentPageDto;
import com.choose.config.UserLocalThread;
import com.choose.constant.CommonConstants;
import com.choose.dishes.dto.AddDishesDto;
import com.choose.dishes.dto.AddShopDto;
import com.choose.dishes.dto.DishesDetailsDto;
import com.choose.dishes.dto.ShopDetailsDto;
import com.choose.dishes.pojos.Dishes;
import com.choose.dishes.pojos.Shops;
import com.choose.dishes.vo.DishesDetailsVo;
import com.choose.dishes.vo.ShopDetailsVo;
import com.choose.dishes.vo.ShopDishesDetailsListVo;
import com.choose.dishes.vo.ShopListVo;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.enums.AuditEnum;
import com.choose.exception.CustomException;
import com.choose.mapper.*;
import com.choose.service.dishes.DishesService;
import com.choose.service.tag.Impl.TagAssociationServiceImpl;
import com.choose.tag.pojos.Tag;
import com.choose.tag.pojos.TagAssociation;
import com.choose.user.dto.DishesReviewDto;
import com.choose.user.dto.ReviewDto;
import com.choose.user.pojos.Review;
import com.choose.user.pojos.User;
import com.choose.user.pojos.UserInfo;
import com.choose.user.vo.ReviewVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/7 上午1:53
 */
@Service
@Transactional
public class DishesServiceImpl extends ServiceImpl<DishesMapper, Dishes> implements DishesService {

    @Resource
    private ReviewMapper reviewMapper;

    @Resource
    private DishesMapper dishesMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ShopsMapper shopsMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private ColumnMapper columnMapper;


    @Resource
    private TagAssociationMapper tagAssociationMapper;

    @Resource
    private TagAssociationServiceImpl tagAssociationServiceImpl;

    /**
     * 获取对应菜品的评论
     */
    @Override
    @SysLog("获取菜品评论")
    public List<ReviewVo> getReview(DishesReviewDto dto) {
        Dishes dishes = dishesMapper.selectById(dto.getDishesId());
        if (Objects.nonNull(dishes)) {
            Page<Review> reviewPage = new Page<>(dto.getPage(), CommonConstants.Common.PANE_SIZE);
            QueryWrapper<Review> reviewQueryWrapper = new QueryWrapper<>();
            reviewQueryWrapper.lambda()
                    .eq(Review::getDishesId, dto.getDishesId())
                    .orderByDesc(Review::getCreateTime);
            List<Review> reviews = reviewMapper.selectPage(reviewPage, reviewQueryWrapper).getRecords();

            // 获取所有评论的用户ID
            List<Long> userIds = reviews.stream()
                    .map(Review::getUserId)
                    .collect(Collectors.toList());

            // 通过userIds查询用户列表
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.lambda()
                    .in(User::getId, userIds);
            List<User> users = userMapper.selectList(userQueryWrapper);
            return reviews.stream()
                    .map(review -> {
                        User user = users.stream()
                                .filter(u -> u.getId().equals(review.getUserId()))
                                .findFirst()
                                .orElse(null);
                        return convertToReviewVo(review, user);
                    })
                    .collect(Collectors.toList());
        }
        throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
    }


    private ReviewVo convertToReviewVo(Review review, User user) {
        ReviewVo reviewVo = new ReviewVo();
        BeanUtils.copyProperties(review, reviewVo);
        BeanUtils.copyProperties(user, reviewVo);
        reviewVo.setCreateTime(review.getCreateTime());
        return reviewVo;
    }

    /**
     * 评论菜品
     */
    @Override
    @SysLog("评论菜品")
    public ReviewVo review(ReviewDto reviewDto) {
        UserInfo user = UserLocalThread.getUser();
        if (Objects.nonNull(user)) {
            Dishes dishes = dishesMapper.selectById(reviewDto.getDishesId());
            if (Objects.nonNull(dishes)) {
                Review review = new Review();
                BeanUtils.copyProperties(reviewDto, review);
                review.setUserId(Long.valueOf(user.getId()));
                reviewMapper.insert(review);
                ReviewVo reviewVo = new ReviewVo();
                BeanUtils.copyProperties(review, reviewVo);
                BeanUtils.copyProperties(user, reviewVo);
                reviewVo.setCreateTime(review.getCreateTime());
                return reviewVo;
            }
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
    }

    /**
     * 添加新的店铺
     */
    @Override
    @SysLog("添加新店铺")
    public void addShop(AddShopDto dto) {
        QueryWrapper<Shops> shopsQueryWrapper = new QueryWrapper<>();
        shopsQueryWrapper.lambda().eq(Shops::getShopName, dto.getShopName());
        List<Shops> shopsList = shopsMapper.selectList(shopsQueryWrapper);
        if (!shopsList.isEmpty()) {
            throw new CustomException(AppHttpCodeEnum.DATA_EXIST);
        }
        Shops shops = new Shops();
        BeanUtils.copyProperties(dto, shops);
        shops.setIsAudit(AuditEnum.UN_AUDIT.getCode());
        shops.setMark("");
        String[] r = shops.getCoordinate().split(",");
        shops.setCoordinate(r[1] + "," + r[0]);
        shops.setUserId(Long.valueOf(UserLocalThread.getUser().getId()));
        shopsMapper.insert(shops);
    }

    /**
     * 添加新的菜品
     */
    @Override
    public void addMark(AddDishesDto dto) {
        QueryWrapper<Dishes> dishesQueryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<Dishes> eqDishes = dishesQueryWrapper.lambda()
                .eq(Dishes::getDishesName, dto.getDishesName())
                .eq(Dishes::getColumnId, dto.getColumId())
                .eq(Dishes::getShop, dto.getShop());
        if (!dishesMapper.selectList(eqDishes).isEmpty()) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if (Objects.isNull(shopsMapper.selectById(dto.getShop()))) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if (Objects.isNull(columnMapper.selectById(dto.getColumId()))) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        Long l = tagMapper.selectCount(tagQueryWrapper.lambda().in(Tag::getId, dto.getTagIds()));
        if (l != (long) dto.getTagIds().size()) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        Dishes dishes = new Dishes();
        dishes.setDishesName(dto.getDishesName());
        dishes.setImage(dto.getImage());
        dishes.setShop(Long.valueOf(dto.getShop()));
        dishes.setColumnId(Long.valueOf(dto.getColumId()));
        dishes.setIsAudit(AuditEnum.UN_AUDIT.getCode());
        this.save(dishes);
        if (Objects.isNull(dishes.getId())) {
            throw new CustomException(AppHttpCodeEnum.DATA_PROBLEM);
        }
        ArrayList<TagAssociation> tagAssociations = new ArrayList<>();
        dto.getTagIds().forEach((String id) -> {
            TagAssociation tagAssociation = new TagAssociation();
            tagAssociation.setTagId(Long.valueOf(id));
            tagAssociation.setModelId(dishes.getId());
            tagAssociations.add(tagAssociation);
        });
        tagAssociationServiceImpl.saveBatch(tagAssociations);
    }

    /**
     * 获取全部店铺
     */
    @Override
    public List<ShopListVo> getShopList() {
        String userId = UserLocalThread.getUser().getId();
        LambdaQueryWrapper<Shops> queryWrapper = new QueryWrapper<Shops>()
                .lambda()
                .eq(Shops::getUserId, userId)
                .eq(Shops::getIsAudit, AuditEnum.AUDIT_SUCCESS.getCode())
                .orderByDesc(Shops::getUpdateTime);

        List<Shops> shops = shopsMapper.selectList(queryWrapper);
        ArrayList<ShopListVo> shopListVos = new ArrayList<>();

        shops.forEach((Shops shop) -> {
            ShopListVo shopListVo = new ShopListVo();
            BeanUtils.copyProperties(shop, shopListVo);
            shopListVo.setId(String.valueOf(shop.getId()));
            shopListVos.add(shopListVo);
        });

        return shopListVos;
    }

    /**
     * 首页获取推荐店铺
     */
    @Override
    public List<ShopListVo> getRecommendShops(CommentPageDto dto) {
        QueryWrapper<Shops> shopsQueryWrapper = new QueryWrapper<>();
        shopsQueryWrapper.lambda()
                .eq(Shops::getIsAudit, AuditEnum.AUDIT_SUCCESS.getCode())
                .orderByDesc(Shops::getMark);

        Page<Shops> page = new Page<>(dto.getPage(), 15);
        Page<Shops> resultPage = shopsMapper.selectPage(page, shopsQueryWrapper);
        List<Shops> records = resultPage.getRecords();
        List<ShopListVo> shopListVos = new ArrayList<>();
        records.forEach((Shops shop) -> {
            ShopListVo shopListVo = new ShopListVo();
            BeanUtils.copyProperties(shop,shopListVo);
            shopListVo.setId(String.valueOf(shop.getId()));
            shopListVos.add(shopListVo);
        });
        return shopListVos;
    }

    /**
     * 获取id店铺的详情
     */
    @Override
    public ShopDetailsVo getShopDetails(ShopDetailsDto dto) {
        Shops shops = shopsMapper.selectById(dto.getShopId());
        if (Objects.nonNull(shops)) {
            ShopDetailsVo shopDetailsVo = new ShopDetailsVo();
            shopDetailsVo.setId(dto.getShopId());
            BeanUtils.copyProperties(shops, shopDetailsVo);

            QueryWrapper<Dishes> dishesQueryWrapper = new QueryWrapper<>();
            dishesQueryWrapper.lambda()
                    .eq(Dishes::getShop, shops.getId())
                    .eq(Dishes::getIsAudit, AuditEnum.AUDIT_SUCCESS.getCode())
                    .orderByDesc(Dishes::getMark);
            List<Dishes> dishes = dishesMapper.selectList(dishesQueryWrapper);

            // 获取 dishesIds
            List<Long> dishesIds = dishes.stream().map(Dishes::getId).toList();
            if(dishesIds.isEmpty()) {
                shopDetailsVo.setDishesDetailsList(new ArrayList<>());
                return shopDetailsVo;
            }

            Map<Long, List<String>> tagsMap = tagMapper.extractTagsByModelIds(dishesIds);
            Map<Long, List<String>> resultMap = new HashMap<>();
            for (Map.Entry<Long, List<String>> entry : tagsMap.entrySet()) {
                Long modelId = entry.getKey();
                Map<String, List<String>> innerMap = (Map<String, List<String>>) entry.getValue();
                List<String> tags =  innerMap.get("tags");
                resultMap.put(modelId, tags);
            }

            // 处理返回结果
            ArrayList<ShopDishesDetailsListVo> shopDishesDetailsListVos = new ArrayList<>();
            System.out.println(tagsMap);
            dishes.forEach((dishesEntity) -> {
                ShopDishesDetailsListVo vo = new ShopDishesDetailsListVo();
                vo.setId(String.valueOf(dishesEntity.getId()));
                vo.setDishesName(dishesEntity.getDishesName());
                vo.setDishesImage(dishesEntity.getImage());
                vo.setDishesTags(resultMap.get(dishesEntity.getId()));
                shopDishesDetailsListVos.add(vo);
            });

            shopDetailsVo.setDishesDetailsList(shopDishesDetailsListVos);
            return shopDetailsVo;
        }

        throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
    }

    /**
     * 根据id获取菜品的详情
     */
    @Override
    public DishesDetailsVo getDishesDetails(DishesDetailsDto dto) {
        Dishes dishes = dishesMapper.selectById(dto.getDishesId());
        if(Objects.nonNull(dishes)) {
            DishesDetailsVo dishesDetailsVo = new DishesDetailsVo();
            BeanUtils.copyProperties(dishes, dishesDetailsVo);
            dishesDetailsVo.setId(String.valueOf(dishes.getId()));
            List<String> dishesIdTag = tagMapper.getDishesIdTag(dishes.getId());
            if(!dishesIdTag.isEmpty()) {
                dishesDetailsVo.setTags(dishesIdTag);
            }
            return dishesDetailsVo;
        }
        throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
    }
}
