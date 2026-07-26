package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BrandCategory {
    private Long id;
    private String nameEn;
    private String nameZh;
    private String key;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer delFlag;

    // For display
    private String name;
    private String label;
}
