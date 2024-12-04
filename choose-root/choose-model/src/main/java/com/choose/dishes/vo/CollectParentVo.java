package com.choose.dishes.vo;
import lombok.Data;

import java.util.List;

/**
 * @author chen
 */
@Data
public class CollectParentVo {
    private String id;
    private String name;
    private List<CollectChildrenVo> children;
}
