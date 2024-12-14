package com.choose.controller.common;

import com.choose.common.UploadVo;
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

    @PostMapping("/v1/getTag")
    public Result getTag() {
        return Result.ok(commonService.getTag());
    }

    @PostMapping("/v1/getWeather")
    public Result getWeather() {
        return Result.ok(commonService.getWeather());
    }

    @PostMapping("/v1/search")
    public Result search(@RequestParam("keyword") String keyword) {
        return Result.ok(commonService.search(keyword));
    }

    @PostMapping("/v1/search_terms")
    public Result searchTerms(@RequestParam("keyword") String keyword) {
        return Result.ok(commonService.searchTerms(keyword));
    }

    @PostMapping("/v1/getSearchHistory")
    public Result getSearchHistory() {
        return Result.ok(commonService.getSearchHistory());
    }

    @PostMapping("/v1/delSearch")
    public Result delSearch() {
        commonService.delSearch();
        return Result.ok();
    }



}
