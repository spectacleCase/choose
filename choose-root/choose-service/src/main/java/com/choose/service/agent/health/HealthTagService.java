package com.choose.service.agent.health;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.agent.pojos.HealthTag;
import com.choose.mapper.HealthTagMapper;
import org.springframework.stereotype.Service;

@Service
public class HealthTagService extends ServiceImpl<HealthTagMapper, HealthTag> {
}
