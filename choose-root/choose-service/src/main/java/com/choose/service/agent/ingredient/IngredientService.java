package com.choose.service.agent.ingredient;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.agent.pojos.Ingredient;
import com.choose.mapper.IngredientMapper;
import org.springframework.stereotype.Service;

@Service
public class IngredientService extends ServiceImpl<IngredientMapper, Ingredient> {
}
