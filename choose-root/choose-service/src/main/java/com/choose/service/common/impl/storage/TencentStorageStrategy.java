package com.choose.service.common.impl.storage;

import com.choose.common.UploadVo;
import com.choose.constant.FileConstant;
import com.choose.service.common.StorageStrategy;
import com.choose.utils.common.CommonUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/27 下午1:14
 */
@Slf4j
@Data
public class TencentStorageStrategy implements StorageStrategy {

    private final static String SEPARATOR = "/";

    private String region;

    private String bucket;

    private COSClient cosClient;

    public TencentStorageStrategy(String accessKey, String secretKey, String region,String bucket) {
        this.region = region;
        this.bucket = bucket;
        BasicCOSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        this.cosClient = new COSClient(cred,clientConfig);
    }


    @Override
    public UploadVo upload(MultipartFile multipartFile) {
        // 文件目录
        String name = UUID.randomUUID().toString().replace("-", "");
        String filename = multipartFile.getOriginalFilename();
        String filepath = null;
        if (filename != null) {
            filepath = builderFilePath("", name + filename.substring(filename.lastIndexOf(".")));
        }
        File file = null;
        File compressedFile = null;
        try {
            //上传文件
            if (filepath != null) {
                file = File.createTempFile(filepath, null);
            }
            if (file != null) {
                multipartFile.transferTo(file);
                compressedFile = CommonUtils.compressImage(file);

            }
            // cosManager.putObject(filepath, file);
            if (compressedFile != null) {
                putObject(filepath, compressedFile);
            }
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath + ", error = " + e);
        } finally {
            if (file != null) {
                //删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    System.out.println("file delete error, filepath = [l" + filepath);
                }
            }
            if (compressedFile != null) {
                // 删除压缩后的临时文件
                boolean delete = compressedFile.delete();
                if (!delete) {
                    System.out.println("compressed file delete error, filepath = " + filepath);
                }
            }

        }

        UploadVo uploadVo = new UploadVo();
        uploadVo.setFileName(FileConstant.COS_HOST + filepath);
        uploadVo.setFilePath(filepath);
        return uploadVo;
    }


    /**
     * 上传对象
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, file);
        return cosClient.putObject(putObjectRequest);
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
