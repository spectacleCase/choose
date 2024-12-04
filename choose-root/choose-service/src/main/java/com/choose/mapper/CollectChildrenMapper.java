package com.choose.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.dishes.pojos.Collectchilren;
import com.choose.dishes.vo.CollectChildrenVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chen
 */
@Mapper
public interface CollectChildrenMapper extends BaseMapper<Collectchilren> {
    List<CollectChildrenVo>  selectCollectchilrenVoList(@Param("userId") Long userId,
                                                        @Param("collectid") Long collectid,
                                                        @Param("offset")int pageNum,
                                                        @Param("size") int pageSize);
}