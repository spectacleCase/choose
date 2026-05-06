package com.choose.service.agent.ingredient;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.agent.pojos.DishIngredient;
import com.choose.mapper.DishIngredientMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishIngredientService extends ServiceImpl<DishIngredientMapper, DishIngredient> {

    public List<DishIngredient> listByDishId(Long dishId) {
        return list(new LambdaQueryWrapper<DishIngredient>().eq(DishIngredient::getDishId, dishId));
    }

    public void replaceByDish(Long dishId, List<DishIngredient> rows) {
        remove(new LambdaQueryWrapper<DishIngredient>().eq(DishIngredient::getDishId, dishId));
        if (rows != null && !rows.isEmpty()) {
            for (DishIngredient r : rows) {
                r.setId(null);
                r.setDishId(dishId);
            }
            saveBatch(rows);
        }
    }
}
