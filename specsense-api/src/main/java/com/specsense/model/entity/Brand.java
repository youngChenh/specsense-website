package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Brand {
    private Long id;
    private Long categoryId;
    private String nameEn;
    private String nameZh;
    private String slug;
    private String logoUrl;
    private String descriptionEn;
    private String descriptionZh;
    private String websiteUrl;
    private Boolean featured;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer delFlag;

    // For display
    private String name;
    private String description;
    private String categoryName;
    private String categoryKey;
}
