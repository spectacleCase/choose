package com.choose.controller.collect;

import com.choose.dishes.dto.CollectDto;
import com.choose.dishes.vo.CollectParentVo;
import com.choose.service.collect.CollectService;
import com.choose.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author chen
 */
@RestController
@Slf4j
@RequestMapping("/choose/collect") // 基础路径，v1 将在每个 postMapping 中单独添加
public class CollectionController {
    @Resource
    private CollectService collectService;

    // 添加收藏
    @PostMapping("/v1/addCollection")
    public Result addCollection(@Valid @RequestBody CollectDto collectDto) {
        collectService.addCollection(collectDto);
        return Result.ok();
    }

    // 删除收藏
    @PostMapping("/v1/deleteCollection")
    public Result deleteCollection(@Valid @RequestBody CollectDto collectDto) {
        collectService.deleteCollection(collectDto);
        return Result.ok();
    }

    // 修改收藏
    @PostMapping("/v1/changeCollection")
    public Result changeCollection(@Valid @RequestBody CollectDto collectDto) {
        collectService.changeCollection(collectDto);
        return Result.ok();
    }

    // 检查用户收藏列表
    @PostMapping("/v1/checkCollection")
    public Result checkCollection() {
        List<CollectParentVo> data = collectService.checkCollection();
        return Result.ok().putData(data);
    }

    // 检查收藏的子项
    @PostMapping("/v1/checkChildren")
    public Result checkChildren(@Valid @RequestBody CollectDto collectDto) {
        CollectParentVo data = collectService.checkChildren(collectDto);
        return Result.ok(data);
    }

    // 添加收藏的子项
    @PostMapping("/v1/addChildren")
    public Result addChildren(@Valid @RequestBody CollectDto collectDto) {
        collectService.addChildren(collectDto);
        return Result.ok();
    }

    // 修改收藏的子项
    @PostMapping("/v1/changeChildren")
    public Result changeChildren(@Valid @RequestBody CollectDto collectDto) {
        collectService.changeChildren(collectDto);
        return Result.ok();
    }

    // 删除收藏的子项
    @PostMapping("/v1/deleteChildren")
    public Result deleteChildren(@Valid @RequestBody CollectDto collectDto) {
        collectService.deleteChildren(collectDto);
        return Result.ok();
    }

    @PostMapping("/v1/checkCollectChildren")
    public Result checkCollectChildren(@Valid @RequestBody CollectDto collectDto){
        return Result.ok().putData(collectService.checkCollectChildren(collectDto));
    }
}