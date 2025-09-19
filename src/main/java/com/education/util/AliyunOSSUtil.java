package com.education.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Component
public class AliyunOSSUtil {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    private OSS ossClient;

    // 初始化OSS客户端
    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 上传文件
     * @param file 上传的文件
     * @param folder 存储的文件夹
     * @return 上传后的文件URL
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        // 构建文件路径
        String key = folder + "/" + fileName;

        // 上传文件
        InputStream inputStream = file.getInputStream();
        ossClient.putObject(bucketName, key, inputStream);

        // 关闭输入流
        inputStream.close();

        // 返回文件URL
        return "https://" + bucketName + "." + endpoint + "/" + key;
    }

    /**
     * 上传文本内容
     * @param content 文本内容
     * @param key 存储的键
     */
    public void uploadString(String content, String key) {
        ossClient.putObject(bucketName, key, new ByteArrayInputStream(content.getBytes()));
    }

    /**
     * 获取文件URL（带签名，有效期默认1小时）
     * @param key 文件键
     * @return 带签名的URL
     */
    public String getFileUrl(String key) {
        // 设置URL过期时间为1小时
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);

        // 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
        return url.toString();
    }

    /**
     * 删除文件
     * @param key 文件键
     */
    public void deleteFile(String key) {
        ossClient.deleteObject(bucketName, key);
    }

    /**
     * 检查文件是否存在
     * @param key 文件键
     * @return 是否存在
     */
    public boolean doesFileExist(String key) {
        return ossClient.doesObjectExist(bucketName, key);
    }

    /**
     * 下载文件
     * @param key 文件键
     * @return 文件输入流
     */
    public InputStream downloadFile(String key) {
        OSSObject ossObject = ossClient.getObject(bucketName, key);
        return ossObject.getObjectContent();
    }

    /**
     * 关闭OSS客户端（一般在应用关闭时调用）
     */
    public void shutdown() {
        ossClient.shutdown();
    }
}
