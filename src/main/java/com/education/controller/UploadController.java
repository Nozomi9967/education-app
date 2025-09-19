package com.education.controller;

import com.education.util.AliyunOSSUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class UploadController {


    @Autowired
    private AliyunOSSUtil aliyunOSSUtil;

    /**
     * 上传文件
     */
    @PostMapping("/img")
    @Operation(summary = "上传文件")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 上传到images文件夹
            return aliyunOSSUtil.uploadFile(file, "images");
        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

    /**
     * 获取文件URL
     */
    @GetMapping("/url")
    @Operation(summary = "获取文件URL")
    public String getFileUrl(@RequestParam("key") String key) {
        return aliyunOSSUtil.getFileUrl(key);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除文件")
    public String deleteFile(@RequestParam("key") String key) {
        aliyunOSSUtil.deleteFile(key);
        return "删除成功";
    }

}
