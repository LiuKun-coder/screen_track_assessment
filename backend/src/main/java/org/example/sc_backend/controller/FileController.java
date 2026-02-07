package org.example.sc_backend.controller;

import org.example.sc_backend.common.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
public class FileController {

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @Value("${file.access-url:http://localhost:8080/api/uploads}")
    private String accessUrl;

    /**
     * 上传文件
     */
    @PostMapping
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error(400, "请选择要上传的文件");
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 按日期分目录存储
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = datePath + "/" + newFilename;

        // 创建目录
        File destDir = new File(uploadPath + "/" + datePath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        // 保存文件
        File destFile = new File(uploadPath + "/" + relativePath);
        file.transferTo(destFile);

        // 返回访问URL
        String fileUrl = accessUrl + "/" + relativePath;
        return Result.success(fileUrl);
    }
}
