package com.choose.config;

import com.choose.config.sensitive_word.LocalWordDeny;
import com.github.houbb.nlp.common.format.impl.CharFormats;
import com.github.houbb.sensitive.word.api.IWordDeny;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.allow.WordAllows;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import com.github.houbb.sensitive.word.support.tag.WordTags;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * <p>
 * SensitiveWord配置
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/6/20 21:04
 */
@Configuration
public class SensitiveWordConfig {

    @Resource
    private LocalWordDeny localWordDeny;

    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                // 默认敏感词  默认白名单  ✅ 关键：添加默认字符格式化
                .wordDeny(WordDenys.chains(WordDenys.defaults(),localWordDeny))
                // .wordDeny((IWordDeny) localWordDeny)
                .wordAllow(WordAllows.defaults())
                .wordTag(WordTags.defaults())
                .init();
    }
}