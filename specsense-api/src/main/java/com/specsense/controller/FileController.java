package com.specsense.controller;

import com.specsense.config.JwtConfig;
import com.specsense.model.vo.FileUploadVO;
import com.specsense.model.vo.Result;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class FileController {

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @Autowired
    private JwtConfig jwtConfig;

    @javax.annotation.PostConstruct
    public void init() {
        // 解析为绝对路径，确保在 Tomcat 环境下路径正确
        File uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize().toFile();
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    private boolean authenticate(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtConfig.parseToken(token);
            return claims != null;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/upload")
    public Result<FileUploadVO> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (!authenticate(request)) {
            return Result.error(401, "Unauthorized");
        }

        if (file.isEmpty()) {
            return Result.error(400, "File is empty");
        }

        try {
            // 使用绝对路径解析上传目录
            Path absoluteUploadPath = Paths.get(uploadPath).toAbsolutePath().normalize();
            File uploadDir = absoluteUploadPath.toFile();
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Get original filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // Save file - 直接从输入流复制，避免临时文件问题
            Path filePath = absoluteUploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return the URL path and original filename
            String fileUrl = "/uploads/" + filename;
            return Result.success(new FileUploadVO(fileUrl, originalFilename));

        } catch (IOException e) {
            return Result.error(500, "Failed to upload file: " + e.getMessage());
        }
    }

    @PostMapping("/upload/batch")
    public Result<String[]> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, HttpServletRequest request) {
        if (!authenticate(request)) {
            return Result.error(401, "Unauthorized");
        }

        String[] urls = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (!file.isEmpty()) {
                try {
                    // 使用绝对路径解析上传目录
                    Path absoluteUploadPath = Paths.get(uploadPath).toAbsolutePath().normalize();
                    File uploadDir = absoluteUploadPath.toFile();
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    String originalFilename = file.getOriginalFilename();
                    String extension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String filename = UUID.randomUUID().toString() + extension;

                    // 直接从输入流复制，避免临时文件问题
                    Path filePath = absoluteUploadPath.resolve(filename);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    urls[i] = "/uploads/" + filename;
                } catch (IOException e) {
                    urls[i] = null;
                }
            }
        }
        return Result.success(urls);
    }
}
