package com.choose_admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.choose.service.tag.TagService;
import com.choose.tag.pojos.Tag;
import com.choose.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 管理端 - 标签管理 (CRUD)。前缀 /choose-admin 与 vite 代理对齐。
 */
@RestController
@RequestMapping("/choose-admin/tag")
public class AdminTagController {

    @Resource
    private TagService tagService;

    /**
     * 标签分页列表
     */
    @GetMapping("/v1/page")
    public Result page(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "20") Integer size) {
        LambdaQueryWrapper<Tag> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(Tag::getTag, keyword);
        }
        int offset = Math.max(0, (page - 1)) * size;
        qw.orderByDesc(Tag::getCreateTime).last("LIMIT " + offset + "," + size);

        LambdaQueryWrapper<Tag> countQw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            countQw.like(Tag::getTag, keyword);
        }
        return Result.ok(Map.of(
                "list", tagService.list(qw),
                "total", tagService.count(countQw)
        ));
    }

    /**
     * 新增 / 编辑 标签
     */
    @PostMapping("/v1/save")
    public Result save(@RequestBody Tag tag) {
        if (tag.getParentTagId() == null) {
            tag.setParentTagId(0L);
        }
        tagService.saveOrUpdate(tag);
        return Result.ok();
    }

    /**
     * 删除标签
     */
    @PostMapping("/v1/delete")
    public Result delete(@RequestBody Map<String, Object> body) {
        Object idObj = body.get("id");
        if (idObj == null) {
            return Result.ok();
        }
        Long id = Long.valueOf(idObj.toString());
        tagService.removeById(id);
        return Result.ok();
    }
}
