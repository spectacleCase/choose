package com.choose.controller.common;

import com.choose.common.dto.GetAddressDitDto;
import com.choose.common.vo.UploadVo;
import com.choose.service.common.CommonService;
import com.choose.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * <p>
 * 通过控制器
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 上午12:59
 */

@RestController
@Slf4j
@RequestMapping("/choose/common")
public class CommonController {

    @Resource
    private ResourceLoader resourceLoader;

    @Resource
    private CommonService commonService;

    @Value("${local.path}")
    private String uploadPhotoPath;

    /**
     * 上传照片
     */
    @PostMapping("/v1/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        UploadVo uploadVo = commonService.upload(file);
        return Result.ok(uploadVo);
    }

    @GetMapping("/v1/getImage")
    public ResponseEntity<?> getImage(@RequestParam(name = "filename", required = true) String filename) {
        org.springframework.core.io.Resource resource = resourceLoader.getResource("file:" + uploadPhotoPath + filename);
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取全部的标签
     */
    @PostMapping("/v1/getTag")
    public Result getTag() {
        return Result.ok(commonService.getTag());
    }

    /**
     * 获取今日的天气情况
     */
    @PostMapping("/v1/getWeather")
    public Result getWeather() {
        return Result.ok(commonService.getWeather());
    }

    /**
     * 查询地址名称和距离
     */
    @PostMapping("/v1/getAddressDit")
    public Result getAddressDit(@RequestBody  GetAddressDitDto dto) {
        return Result.ok(commonService.getAddressDit(dto));
    }

    /**
     * 搜索
     */
    @PostMapping("/v1/search")
    public Result search(@RequestParam("keyword") String keyword) {
        return Result.ok(commonService.search(keyword));
    }

    /**
     * 搜索词推荐
     */
    @PostMapping("/v1/search_terms")
    public Result searchTerms(@RequestParam("keyword") String keyword) {
        return Result.ok(commonService.searchTerms(keyword));
    }

    /**
     * 获取搜索列表
     */
    @PostMapping("/v1/getSearchHistory")
    public Result getSearchHistory() {
        return Result.ok(commonService.getSearchHistory());
    }

    /**
     * 删除全部搜索记录
     */
    @PostMapping("/v1/delSearch")
    public Result delSearch() {
        commonService.delSearch();
        return Result.ok();
    }

    /**
     * 食物热量识别
     */
    @GetMapping("/v1/heatRecognition")
    public Result heatRecognition(@RequestParam(name = "foodImage") String foodImage) {
        return Result.ok(commonService.heatRecognition(foodImage));
    }


}
