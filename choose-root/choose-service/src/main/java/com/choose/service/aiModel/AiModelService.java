package com.choose.service.aiModel;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.choose.ai.pojo.AiModel;
import com.choose.mapper.AiModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/6/13 17:44
 */
@Service
@Slf4j
public class AiModelService extends ServiceImpl<AiModelMapper, AiModel> {


    @Resource
    private AiModelSettingService aiModelSettingService;

    @PostConstruct
    public void init() {
        log.info("init AI Model");
        List<AiModel> aiModels = ChainWrappers.lambdaQueryChain(baseMapper).eq(AiModel::getIsDelete, false).list();
        aiModelSettingService.init(aiModels);

    }




}
