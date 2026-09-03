package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Product {
    private Long id;
    private Long categoryId;
    private String nameEn;
    private String nameZh;
    private String slug;
    private String descriptionEn;
    private String descriptionZh;
    private String imageUrl;
    private String imageUrls;
    private String downloadPdfUrl;
    private String specsJson;
    private Boolean featured;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer delFlag;
    private String highlights;
    private String applications;
    private String overviewModules;

    // For display
    private String name;
    private String description;
    private String categoryName;
    private String categoryKey;
}
