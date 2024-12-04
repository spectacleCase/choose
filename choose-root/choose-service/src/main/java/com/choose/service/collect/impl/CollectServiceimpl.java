package com.choose.service.collect.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.config.UserLocalThread;
import com.choose.constant.CommonConstants;
import com.choose.constant.FileConstant;
import com.choose.dishes.dto.CollectDto;
import com.choose.dishes.pojos.Collect;
import com.choose.dishes.pojos.Collectchilren;
import com.choose.dishes.vo.CollectChildrenVo;
import com.choose.dishes.vo.CollectParentVo;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import com.choose.mapper.CollectChildrenMapper;
import com.choose.mapper.CollectMapper;
import com.choose.mapper.DishesMapper;
import com.choose.service.collect.CollectService;
import com.choose.user.pojos.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;


/**
 * @author chen
 */
@Service
@Slf4j
public class CollectServiceimpl extends ServiceImpl<CollectMapper, Collect> implements CollectService {

    @Resource
    private CollectMapper collectMapper;

    @Resource
    private CollectChildrenMapper collectChildrenMapper;

    @Resource
    private DishesMapper dishesMapper;

    @Override
    public void addCollection(CollectDto collectDto) {
        try {
            UserInfo user = UserLocalThread.getUser();
            Collect collect = new Collect();
            collect.setName(collectDto.getName());
            collect.setUserid(Long.valueOf(user.getId()));
            collectMapper.insert(collect);
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public void deleteCollection(CollectDto collectDto) {
        try {
            UserInfo user = UserLocalThread.getUser();
            collectMapper.delete(new LambdaQueryWrapper<Collect>()
                    .eq(Collect::getUserid, user.getId())
                    .eq(Collect::getId, collectDto.getCollectId()));
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public void changeCollection(CollectDto collectDto) {
        try {
            UserInfo user = UserLocalThread.getUser();
            Collect collect = new Collect();
            collect.setName(collectDto.getName());
            collect.setId(Long.valueOf(collectDto.getCollectId()));
            collectMapper.update(collect, new LambdaUpdateWrapper<Collect>()
                    .eq(Collect::getUserid, user.getId())
                    .eq(Collect::getId, collectDto.getCollectId()));
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public List<CollectParentVo> checkCollection() {
        try {
            UserInfo user = UserLocalThread.getUser();
            return collectMapper.selectCollectVoList(Long.valueOf(user.getId()));
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public CollectParentVo checkChildren(CollectDto collectDto) {
        try {
            UserInfo user = UserLocalThread.getUser();
            CollectParentVo collectParent = new CollectParentVo();
            int offset = (collectDto.getPageNum() - 1) * CommonConstants.Common.PANE_SIZE;
            List<CollectChildrenVo> collectDate = collectChildrenMapper.selectCollectchilrenVoList(Long.valueOf(user.getId()), Long.valueOf(collectDto.getCollectId()),offset, 10);
            List<CollectChildrenVo> transformedCollectChildrenVos = collectDate.stream()
                    .map(collect -> {
                        CollectChildrenVo newCollectChildrenVo = new CollectChildrenVo();
                        newCollectChildrenVo.setId(collect.getId());
                        newCollectChildrenVo.setCoordinate(collect.getCoordinate());
                        newCollectChildrenVo.setDishesName(collect.getDishesName());
                        // 确保 collect.getDishesImage() 不为 null
                        String dishesImageWithHost = Optional.ofNullable(collect.getDishesImage())
                                .map(image -> FileConstant.COS_HOST + image)
                                .orElse(null);
                        newCollectChildrenVo.setDishesImage(dishesImageWithHost);
                        newCollectChildrenVo.setDishId(collect.getDishId());
                        return newCollectChildrenVo;
                    })
                    .toList();
            collectParent.setId(collectDto.getCollectId());
            collectParent.setChildren(transformedCollectChildrenVos);
            collectParent.setName(collectDto.getName());
            return collectParent;
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public void addChildren(CollectDto collectDto) {
        try {
            UserInfo user = UserLocalThread.getUser();
            Collectchilren collectchilren = new Collectchilren();
            String filePath = collectDto.getDishesImage().substring(collectDto.getDishesImage().indexOf(".com/") + 5);
            collectchilren.setParentId(Long.valueOf(collectDto.getCollectId()));
            collectchilren.setUserid(Long.valueOf(user.getId()));
            collectchilren.setDishesName(collectDto.getDishesName());
            collectchilren.setDishesImage(filePath);
            collectchilren.setCoordinate(dishesMapper.selectCoordinate(Long.valueOf(collectDto.getDishId())));
            collectchilren.setDishId(Long.valueOf(collectDto.getDishId()));
            collectChildrenMapper.insert(collectchilren);
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public void changeChildren(CollectDto collectDto) {
        try {
            UserInfo user = UserLocalThread.getUser();
            Collectchilren collectchilren = new Collectchilren();
            collectchilren.setParentId(Long.valueOf(collectDto.getCollectId()));
            collectChildrenMapper.update(collectchilren, new LambdaQueryWrapper<Collectchilren>()
                    .eq(Collectchilren::getUserid, user.getId())
                    .eq(Collectchilren::getId, collectDto.getCollectChildrenId()));
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public void deleteChildren(CollectDto collectDto) {
        try {
            UserInfo user = UserLocalThread.getUser();
            if (StringUtils.isNotBlank(collectDto.getCollectChildrenId())){
                collectChildrenMapper.delete(new LambdaQueryWrapper<Collectchilren>()
                        .eq(Collectchilren::getUserid, user.getId())
                        .eq(Collectchilren::getId, collectDto.getCollectChildrenId()));
            }else {
                 collectChildrenMapper.delete(new LambdaQueryWrapper<Collectchilren>()
                        .eq(Collectchilren::getUserid, user.getId())
                        .eq(Collectchilren::getDishId, collectDto.getDishId()));
            }
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public CollectChildrenVo checkCollectChildren(CollectDto collectDto) {
        try {
            UserInfo user = UserLocalThread.getUser();
            CollectChildrenVo  collectChildrenVo= new CollectChildrenVo();
            List<Collectchilren> date= collectChildrenMapper.selectList(new LambdaQueryWrapper<Collectchilren>()
                    .eq(Collectchilren::getUserid, user.getId())
                    .eq(Collectchilren::getDishId, collectDto.getDishId()));
            collectChildrenVo.setIsCollect(!date.isEmpty());
            return collectChildrenVo;

        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }

    }
}