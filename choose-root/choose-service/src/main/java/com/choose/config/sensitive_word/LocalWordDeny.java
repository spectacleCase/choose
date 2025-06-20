package com.choose.config.sensitive_word;

import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import org.springframework.core.io.ClassPathResource;
import com.github.houbb.sensitive.word.api.IWordDeny;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


/**
 * @author 桌角的眼镜
 */
@Component
public class LocalWordDeny  implements IWordDeny {

    @Override
    public List<String> deny() {
        // 从文件读取敏感词
        try {
            ClassPathResource resource = new ClassPathResource("userdict.txt");
            Path filePath = Path.of(resource.getURI());
            return Files.readAllLines(filePath);
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.DATA_PROBLEM);
        }
    }
}