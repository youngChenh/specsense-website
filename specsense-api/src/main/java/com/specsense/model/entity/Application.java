package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Application {
    private Long id;
    private String type;
    private String titleEn;
    private String titleZh;
    private String descriptionEn;
    private String descriptionZh;
    private String icon;
    private String imageUrl;
    private String linkedCategoryKeys;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For display
    private String title;
    private String description;
}
