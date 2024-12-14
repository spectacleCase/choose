package com.choose.service.common.impl.storage;

import com.choose.common.UploadVo;
import com.choose.service.common.StorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * <p>
 * 本地缓存策略
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/3 上午11:39
 */
@Slf4j
public class LocalStorageStrategy implements StorageStrategy {
    private final static String SEPARATOR = "/";

    private final String path;

    private final String localGetImage;

    public LocalStorageStrategy(String path, String localGetImage) {
        this.path = path;
        this.localGetImage = localGetImage;
    }

    @Override
    public UploadVo upload(MultipartFile filePath) {
        if (filePath.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        String filepath = null;
        String destinationPath = "";

        try {
            // 获取文件名
            String fileName = filePath.getOriginalFilename();
            String name = UUID.randomUUID().toString().replace("-", "");

            if (fileName != null) {
                filepath = builderFilePath("", name + fileName.substring(fileName.lastIndexOf(".")));
            }

            // 设置目标文件路径
            destinationPath = path + filepath;
            File destinationFile = new File(destinationPath);

            // 检查并创建目标文件夹
            File destinationDir = destinationFile.getParentFile();
            if (!destinationDir.exists()) {
                if (!destinationDir.mkdirs()) {
                    throw new IOException("无法创建目标文件夹: " + destinationDir.getAbsolutePath());
                }
            }

            // 保存文件到本地
            filePath.transferTo(destinationFile);

        } catch (IOException e) {
            log.error("file upload error, filepath = " + filepath + ", error = " + e);
            throw new RuntimeException("文件上传失败", e);
        }

        UploadVo uploadVo = new UploadVo();
        uploadVo.setFileName(localGetImage + filepath);
        uploadVo.setFilePath(localGetImage+ filepath);
        return uploadVo;
    }


    public String builderFilePath(String dirPath, String fileName) {
        StringBuilder stringBuilder = new StringBuilder(50);
        if (!StringUtils.isEmpty(dirPath)) {
            stringBuilder.append(dirPath).append(SEPARATOR);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        stringBuilder.append(todayStr).append(SEPARATOR);
        stringBuilder.append(fileName);
        return stringBuilder.toString();
    }
}
