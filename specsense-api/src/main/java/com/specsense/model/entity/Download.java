package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Download {
    private Long id;
    private String category;
    private String nameEn;
    private String nameZh;
    private String descriptionEn;
    private String descriptionZh;
    private String fileUrl;
    private String fileSize;
    private String originalFilename;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer delFlag;

    // For display
    private String name;
    private String description;
    private String categoryName;
}
