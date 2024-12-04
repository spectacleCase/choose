package com.choose.service.collect;

import com.baomidou.mybatisplus.extension.service.IService;
import com.choose.dishes.dto.CollectDto;
import com.choose.dishes.pojos.Collect;
import com.choose.dishes.vo.CollectChildrenVo;
import com.choose.dishes.vo.CollectParentVo;

import java.util.List;

/**
 * @author chen
 */
public interface CollectService  extends IService<Collect> {


    void addCollection(CollectDto collectDto);

    void deleteCollection(CollectDto collectDto);

    void changeCollection(CollectDto collectDto);

    List<CollectParentVo> checkCollection();

    CollectParentVo checkChildren(CollectDto collectDto);

    void addChildren(CollectDto collectDto);

    void changeChildren(CollectDto collectDto);

    void deleteChildren(CollectDto collectDto);

    CollectChildrenVo checkCollectChildren(CollectDto collectDto);
}
