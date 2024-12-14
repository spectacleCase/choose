package com.choose.service.common;

import com.choose.common.dto.GetAddressDitDto;
import com.choose.common.vo.GetAddressDitVo;
import com.choose.common.vo.TipsVo;
import com.choose.common.vo.UploadVo;
import com.choose.common.vo.WeatherVo;
import com.choose.search.vo.SearchVo;
import com.choose.tag.pojos.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 上午1:12
 */
public interface CommonService {

    /**
     * 上传照片
     */
    UploadVo upload(MultipartFile file);

    /**
     * 每天随机健康小知识
     */
    TipsVo healthTips();

    /**
     * 获取全部的标签
     */
    List<Tag> getTag();

    /**
     * 获取本日的天气情况
     */
    WeatherVo getWeather();

    /**
     * 搜索
     */
    List<SearchVo> search(String keyword);

    /**
     * 搜索词推荐
     */
    List<String> searchTerms(String keyword);

    /**
     * 获取搜索列表
     */
    List<String> getSearchHistory();

    /**
     * 删除全部搜索记录
     */
    void delSearch();

    /**
     * 查询地址名称和距离
     */
    GetAddressDitVo getAddressDit(GetAddressDitDto dto);
}
