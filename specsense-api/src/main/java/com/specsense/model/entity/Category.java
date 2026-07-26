package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private String nameEn;
    private String nameZh;
    private String key;
    private Long parentId;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer delFlag;

    // For tree structure
    private String name;
    private String label;
}
