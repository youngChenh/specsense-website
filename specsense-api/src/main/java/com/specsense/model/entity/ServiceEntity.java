package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServiceEntity {
    private Long id;
    private String type;
    private String titleEn;
    private String titleZh;
    private String descriptionEn;
    private String descriptionZh;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer delFlag;

    // For display
    private String title;
    private String description;
}
