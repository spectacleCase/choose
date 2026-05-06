package com.choose.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.agent.pojos.Ingredient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IngredientMapper extends BaseMapper<Ingredient> {
}
