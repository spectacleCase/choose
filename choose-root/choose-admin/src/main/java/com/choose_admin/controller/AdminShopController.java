package com.choose_admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.choose.dishes.pojos.Shops;
import com.choose.mapper.ShopsMapper;
import com.choose.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 管理端 - 店铺管理 (CRUD)。前缀 /choose-admin 与 vite 代理对齐。
 * 与 AdminDishesController 中的"店铺审核"接口分离，本类仅负责店铺基础信息维护。
 */
@RestController
@RequestMapping("/choose-admin/shop")
public class AdminShopController {

    @Resource
    private ShopsMapper shopsMapper;

    /**
     * 店铺分页列表 (默认仅查已审核通过的店铺)
     */
    @GetMapping("/v1/page")
    public Result page(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) Integer isAudit) {
        LambdaQueryWrapper<Shops> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w.like(Shops::getShopName, keyword)
                    .or().like(Shops::getCoordinate, keyword));
        }
        if (isAudit != null) {
            qw.eq(Shops::getIsAudit, isAudit);
        }
        int offset = Math.max(0, (page - 1)) * size;
        qw.orderByDesc(Shops::getCreateTime).last("LIMIT " + offset + "," + size);

        LambdaQueryWrapper<Shops> countQw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            countQw.and(w -> w.like(Shops::getShopName, keyword)
                    .or().like(Shops::getCoordinate, keyword));
        }
        if (isAudit != null) {
            countQw.eq(Shops::getIsAudit, isAudit);
        }

        return Result.ok(Map.of(
                "list", shopsMapper.selectList(qw),
                "total", shopsMapper.selectCount(countQw)
        ));
    }

    /**
     * 新增 / 编辑 店铺
     */
    @PostMapping("/v1/save")
    public Result save(@RequestBody Shops shop) {
        if (shop.getId() == null) {
            // 新建店铺默认审核通过(管理员录入)
            if (shop.getIsAudit() == null) {
                shop.setIsAudit(1);
            }
            shopsMapper.insert(shop);
        } else {
            shopsMapper.updateById(shop);
        }
        return Result.ok();
    }

    /**
     * 删除店铺
     */
    @PostMapping("/v1/delete")
    public Result delete(@RequestBody Map<String, Object> body) {
        Object idObj = body.get("id");
        if (idObj == null) {
            return Result.ok();
        }
        Long id = Long.valueOf(idObj.toString());
        shopsMapper.deleteById(id);
        return Result.ok();
    }
}
