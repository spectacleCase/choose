package com.choose.service.aiModel;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/1/9 23:31
 */
public interface ModelService<T> {

    T process(String fun,String ...input);
}
