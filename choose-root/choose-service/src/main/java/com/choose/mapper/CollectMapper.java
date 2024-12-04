package com.choose.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.dishes.pojos.Collect;
import com.choose.dishes.vo.CollectParentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chen
 */
@Mapper
public interface CollectMapper  extends BaseMapper<Collect> {
    List<CollectParentVo> selectCollectVoList(@Param("userId") Long userId);

}
