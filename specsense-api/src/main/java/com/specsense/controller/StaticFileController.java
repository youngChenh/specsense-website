package com.specsense.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
@RequestMapping("/uploads")
public class StaticFileController {

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String filename,
            @RequestParam(required = false) String name) {
        try {
            Path filePath = Paths.get(uploadPath, filename).normalize();
            File file = filePath.toFile();

            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);

            String contentType;
            try {
                contentType = Files.probeContentType(filePath);
            } catch (IOException e) {
                contentType = "application/octet-stream";
            }

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Use original filename if provided, otherwise use stored filename
            String originalFilename = name;
            if (originalFilename == null || originalFilename.isEmpty()) {
                originalFilename = file.getName();
            }

            // Encode filename for Content-Disposition header
            String encodedFilename = URLEncoder.encode(originalFilename, "UTF-8")
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{subdir}/{filename}")
    public ResponseEntity<Resource> getFileWithSubdir(
            @PathVariable String subdir,
            @PathVariable String filename,
            @RequestParam(required = false) String name) {
        try {
            Path filePath = Paths.get(uploadPath, subdir, filename).normalize();
            File file = filePath.toFile();

            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);

            String contentType;
            try {
                contentType = Files.probeContentType(filePath);
            } catch (IOException e) {
                contentType = "application/octet-stream";
            }

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Use original filename if provided, otherwise use stored filename
            String originalFilename = name;
            if (originalFilename == null || originalFilename.isEmpty()) {
                originalFilename = file.getName();
            }

            // Encode filename for Content-Disposition header
            String encodedFilename = URLEncoder.encode(originalFilename, "UTF-8")
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
