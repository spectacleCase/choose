package com.choose.service.common.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.annotation.SysLog;
import com.choose.common.*;
import com.choose.common.dto.GetAddressDitDto;
import com.choose.common.vo.GetAddressDitVo;
import com.choose.common.vo.TipsVo;
import com.choose.common.vo.UploadVo;
import com.choose.common.vo.WeatherVo;
import com.choose.config.UserLocalThread;
import com.choose.constant.FileConstant;
import com.choose.dishes.pojos.Shops;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import com.choose.mapper.*;
import com.choose.search.pojos.SearchHistory;
import com.choose.search.vo.SearchDishesVo;
import com.choose.search.vo.SearchVo;
import com.choose.service.common.CommonService;
import com.choose.service.common.StorageStrategy;
import com.choose.tag.pojos.Tag;
import com.choose.textAbstract.TestTextAbstract;
import com.choose.user.pojos.User;
import com.choose.user.pojos.UserInfo;
import com.choose.utils.common.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 上午1:15
 */
@Service
@Slf4j
public class CommonServiceImpl extends ServiceImpl<UserMapper, User> implements CommonService {

    @Resource
    private StorageStrategy storageStrategy;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private ShopsMapper shopsMapper;

    @Resource
    private DishesMapper dishesMapper;

    @Resource
    private SearchHistoryMapper searchHistoryMapper;


    /**
     * 上传照片
     */
    @Override
    public UploadVo upload(MultipartFile multipartFile) {
        return storageStrategy.upload(multipartFile);
    }

    /**
     * 每天随机健康小知识
     */
    @Override
    @SysLog("每天健康小知识")
    public TipsVo healthTips() {
        TipsVo tipsVo = new TipsVo();
        StringBuilder extracted = TestTextAbstract.getHealthTips();
        // StringBuilder e = new StringBuilder("吃太多糖不仅会让你发胖，还会加速皮肤老化哦！");
        // tipsVo.setTips(e);
        tipsVo.setTips(extracted);
        return tipsVo;
    }

    /**
     * 获取全部的标签
     */
    @Override
    @SysLog("获取全部标签")
    public List<Tag> getTag() {
        // 将标签列表转换为Map，键是父级标签名称，值是该父级标签下的所有子标签列表
        return tagMapper.selectList(null);
        // if (tagList.isEmpty()) {
        //     throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        // }
        // return tagList.stream()
        //         .filter(tag -> tag.getParentTagId() != null)
        //         .collect(Collectors.groupingBy(tag -> tagMapper.selectOne(Wrappers.<Tag>lambdaQuery()
        //                 .eq(Tag::getId, tag.getParentTagId())).getTag()));
    }


    /**
     * 获取本日的天气情况
     */
    @Override
    @SysLog("获取天气情况")
    public WeatherVo getWeather() {
        return CommonUtils.newGetWeather();
    }

    /**
     * 查询地址名称和距离
     */
    @Override
    public GetAddressDitVo getAddressDit(GetAddressDitDto dto) {
        GetAddressDitVo getAddressDitVo = new GetAddressDitVo();
        getAddressDitVo.setAddressName(com.choose.common.CommonUtils.geocode(dto.target()));
        getAddressDitVo.setDistance(com.choose.common.CommonUtils.getDistance(dto.address(), dto.target()));
        return getAddressDitVo ;

    }

    @Override
    @SysLog("搜索")
    public List<SearchVo> search(String keyword) {
        if(Objects.isNull(keyword) || keyword.isEmpty()) {
            return new ArrayList<>();
        }
        List<SearchVo> searchVos = dishesMapper.searchShopsAndDishes(keyword);
        for (SearchVo searchVo : searchVos) {
            if(!isNotHttp(searchVo.getShopImage())) {
                searchVo.setShopImage(FileConstant.COS_HOST + searchVo.getShopImage());
            }
            setUrl(searchVo.getDishesList());
        }
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setUserId(Long.valueOf(UserLocalThread.getUser().getId()));
        searchHistory.setKeyword(keyword);
        searchHistoryMapper.insert(searchHistory);
        return searchVos;
    }

    public void setUrl(List<SearchDishesVo> dishesVos) {
        for (SearchDishesVo dishesVo : dishesVos) {
            if(!isNotHttp(dishesVo.getImage())) {
                dishesVo.setImage(FileConstant.COS_HOST + dishesVo.getImage());
            }
        }
    }
    public  Boolean isNotHttp(String url) {
        if(!StringUtils.isEmpty(url)) {
            if("http".equalsIgnoreCase(url)) {
                return false;
            }
            return !"https".equalsIgnoreCase(url);
        }
        return true;
    }

    @Override
    @SysLog("搜索词推荐")
    public List<String> searchTerms(String keyword) {
        if(Objects.isNull(keyword) || keyword.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Shops> name = new QueryWrapper<Shops>().lambda().like(Shops::getShopName, "%" + keyword + "%");
        List<Shops> searchTermList = shopsMapper.selectList(name);
        List<SearchTerm> searchDishesTerms = dishesMapper.selectSearchTerm(keyword);
        List<String> searchTerms = new ArrayList<>();
        for (Shops shops : searchTermList) {
            // searchTerm.setId(String.valueOf(shops.getId()));
            searchTerms.add(shops.getShopName());
        }
        for (SearchTerm searchDishesTerm : searchDishesTerms) {
            searchTerms.add(searchDishesTerm.getName());
        }
        // searchTerms.addAll(searchDishesTerms);
        // SearchHistory searchHistory = new SearchHistory();
        // searchHistory.setUserId(Long.valueOf(UserLocalThread.getUser().getId()));
        // searchHistory.setKeyword(keyword);
        // searchHistoryMapper.insert(searchHistory);
        return searchTerms;
    }

    @Override
    @SysLog("获取搜索历史列表")
    public List<String> getSearchHistory() {
        UserInfo user = UserLocalThread.getUser();
        if(Objects.nonNull(user)) {
            QueryWrapper<SearchHistory> r = new QueryWrapper<>();
            r.lambda().eq(SearchHistory::getUserId, user.getId()).orderByDesc(SearchHistory::getCreateTime);
            List<SearchHistory> searchHistories = searchHistoryMapper.selectList(r);
            List<String> strings = new ArrayList<>();
            for (SearchHistory searchHistory : searchHistories) {
                strings.add(searchHistory.getKeyword());
            }
            return strings;
        }
        throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
    }

    /**
     * 删除全部搜索记录
     */
    @Override
    @SysLog("删除全部搜索记录")
    public void delSearch() {
        UserInfo user = UserLocalThread.getUser();
        if(Objects.nonNull(user)) {
            QueryWrapper<SearchHistory> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SearchHistory::getUserId, user.getId()).eq(SearchHistory::getIsDelete,0);

            // 创建 LambdaUpdateWrapper 并设置更新条件
            LambdaUpdateWrapper<SearchHistory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SearchHistory::getUserId, user.getId())
                    .set(SearchHistory::getIsDelete, 1);

            // 执行更新操作
            searchHistoryMapper.update(null, updateWrapper);
            return;
        }
        throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
    }
}


