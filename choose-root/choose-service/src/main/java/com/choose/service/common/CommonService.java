package com.choose.service.common;

import com.choose.common.dto.GetAddressDitDto;
import com.choose.common.vo.*;
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

    UploadVo upload(MultipartFile file);

    TipsVo healthTips();

    List<Tag> getTag();

    WeatherVo getWeather();

    List<SearchVo> search(String keyword);

    List<String> searchTerms(String keyword);

    List<String> getSearchHistory();

    GetAddressDitVo getAddressDit(GetAddressDitDto dto);

    HeatRecognitionVo heatRecognition(String foodImage);

    void delSearch();
}
