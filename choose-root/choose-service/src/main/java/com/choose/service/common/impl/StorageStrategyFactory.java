package com.choose.service.common.impl;

import com.choose.service.common.StorageStrategy;
import com.choose.service.common.impl.storage.LocalStorageStrategy;
import com.choose.service.common.impl.storage.MinioStorageStrategy;
import com.choose.service.common.impl.storage.TencentStorageStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 存储工厂
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/27 下午1:03
 */
@Configuration
public class StorageStrategyFactory {

    /**
     * 存储策略
     */
    @Value("${system.storage_strategy}")
    private String strategy;

    /**
     * 腾讯存储key
     */
    @Value("${cos.client.accessKey}")
    private String txAccessKey;

    /**
     * 腾讯存储key
     */
    @Value("${cos.client.secretKey}")
    private String txSecretKey;

    /**
     * 腾讯存储区域
     */
    @Value("${cos.client.region}")
    private String txRegion;

    /**
     * 腾讯存储桶名
     */
    @Value("${cos.client.bucket}")
    private String txBucket;


    /**
     * minio存储Ip端口
     */
    @Value("${minio.endpoint}")
    private String miEndpoint;

    /**
     * minio存储key
     */
    @Value("${minio.accessKey}")
    private String miAccessKey;

    /**
     * minio存储key
     */
    @Value("${minio.secretKey}")
    private String miSecretKey;

    /**
     * minio桶名称
     */
    @Value("${minio.bucketName}")
    private String miBucket;

    /**
     * minio存储路径
     */
    @Value("${minio.readPath}")
    private String miReadPath;

    @Value("${local.path}")
    private String localPath;

    @Value("${local.get_image}")
    private String localGetImage;


    @Bean
    public StorageStrategy storageStrategy() {

        switch (strategy) {
            case "tencentCos":
                return new TencentStorageStrategy(txAccessKey, txSecretKey, txRegion, txBucket);
            case "minio":
                return new MinioStorageStrategy(miEndpoint, miAccessKey, miSecretKey, miBucket, miReadPath);
            case "local":
                return new LocalStorageStrategy(localPath,localGetImage);
            default:
                return new LocalStorageStrategy(localPath,localGetImage);
        }
    }
}