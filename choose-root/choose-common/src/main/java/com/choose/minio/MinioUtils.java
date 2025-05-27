package com.choose.minio;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * minio工具类
 *
 * @author 桌角的眼镜
 */
// @Component
@Slf4j
public class MinioUtils {
    // @Resource
    // private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * description: 判断bucket是否存在，不存在则创建
     */
    // public void existBucket(String name) {
    //     try {
    //         boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
    //         if (!exists) {
    //             minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
    //         }
    //     } catch (Exception e) {
    //         log.error("minio error", e);
    //         throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
    //     }
    // }
    //
    // /**
    //  * 创建存储bucket
    //  *
    //  * @param bucketName 存储bucket名称
    //  * @return Boolean
    //  */
    // public Boolean makeBucket(String bucketName) {
    //     try {
    //         minioClient.makeBucket(MakeBucketArgs.builder()
    //                 .bucket(bucketName)
    //                 .build());
    //     } catch (Exception e) {
    //         log.error("minio error", e);
    //         return false;
    //     }
    //     return true;
    // }
    //
    // /**
    //  * 删除存储bucket
    //  *
    //  * @param bucketName 存储bucket名称
    //  * @return Boolean
    //  */
    // public Boolean removeBucket(String bucketName) {
    //     try {
    //         minioClient.removeBucket(RemoveBucketArgs.builder()
    //                 .bucket(bucketName)
    //                 .build());
    //     } catch (Exception e) {
    //         log.error("minio error", e);
    //         return false;
    //     }
    //     return true;
    // }
    //
    // /**
    //  * description: 上传文件
    //  * 文件名字可以加斜杠
    //  */
    // public List<String> upload(MultipartFile[] multipartFile) {
    //     List<String> names = new ArrayList<>(multipartFile.length);
    //     for (MultipartFile file : multipartFile) {
    //         String fileName = file.getOriginalFilename();
    //         if(Objects.isNull(fileName)){
    //             return names;
    //         }
    //         String[] split = fileName.split("\\.");
    //         if (split.length > 1) {
    //             fileName = split[0] + "_" + System.currentTimeMillis() + "." + split[1];
    //         } else {
    //             fileName = fileName + System.currentTimeMillis();
    //         }
    //         InputStream in = null;
    //         try {
    //             in = file.getInputStream();
    //             minioClient.putObject(PutObjectArgs.builder()
    //                     .bucket(bucketName)
    //                     .object(fileName)
    //                     .stream(in, in.available(), -1)
    //                     .contentType(file.getContentType())
    //                     .build()
    //             );
    //         } catch (Exception e) {
    //             log.error("minio error", e);
    //         } finally {
    //             if (in != null) {
    //                 try {
    //                     in.close();
    //                 } catch (IOException e) {
    //                     log.error("minio error", e);
    //                 }
    //             }
    //         }
    //         names.add(fileName);
    //     }
    //     return names;
    // }
    //
    // /**
    //  * description: 下载文件
    //  */
    // public ResponseEntity<byte[]> download(String fileName) {
    //     ResponseEntity<byte[]> responseEntity = null;
    //     InputStream in = null;
    //     ByteArrayOutputStream out = null;
    //     try {
    //         in = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
    //         out = new ByteArrayOutputStream();
    //         IOUtils.copy(in, out);
    //         //封装返回值
    //         byte[] bytes = out.toByteArray();
    //         HttpHeaders headers = new HttpHeaders();
    //         try {
    //             headers.add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
    //         } catch (UnsupportedEncodingException e) {
    //             log.error("minio error", e);
    //         }
    //         headers.setContentLength(bytes.length);
    //         headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    //         headers.setAccessControlExposeHeaders(List.of("*"));
    //         responseEntity = new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
    //     } catch (Exception e) {
    //         log.error("minio error", e);
    //         throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
    //     } finally {
    //         try {
    //             if (in != null) {
    //                 try {
    //                     in.close();
    //                 } catch (IOException e) {
    //                     log.error("minio error", e);
    //                 }
    //             }
    //             if (out != null) {
    //                 out.close();
    //             }
    //         } catch (IOException e) {
    //             log.error("minio error", e);
    //         }
    //     }
    //     return responseEntity;
    // }
    //
    // /**
    //  * 查看文件对象
    //  *
    //  * @param bucketName 存储bucket名称
    //  * @return 存储bucket内文件对象信息
    //  */
    // public List<ObjectItem> listObjects(String bucketName) {
    //     Iterable<Result<Item>> results = minioClient.listObjects(
    //             ListObjectsArgs.builder().bucket(bucketName).build());
    //     List<ObjectItem> objectItems = new ArrayList<>();
    //     try {
    //         for (Result<Item> result : results) {
    //             Item item = result.get();
    //             ObjectItem objectItem = new ObjectItem();
    //             objectItem.setObjectName(item.objectName());
    //             objectItem.setSize(item.size());
    //             objectItems.add(objectItem);
    //         }
    //     } catch (Exception e) {
    //         log.error("minio error", e);
    //         return null;
    //     }
    //     return objectItems;
    // }
    //
    // /**
    //  * 批量删除文件对象
    //  *
    //  * @param bucketName 存储bucket名称
    //  * @param objects    对象名称集合
    //  */
    // public Iterable<Result<DeleteError>> removeObjects(String bucketName, List<String> objects) {
    //     List<DeleteObject> dos = objects.stream().map(DeleteObject::new).collect(Collectors.toList());
    //     return minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
    // }
}


