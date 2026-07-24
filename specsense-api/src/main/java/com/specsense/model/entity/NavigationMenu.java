package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NavigationMenu {
    private Long id;
    private String key;
    private String position; // header, footer
    private String labelEn;
    private String labelZh;
    private String path;
    private String icon;
    private Integer sortOrder;
    private Long parentId;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<NavigationMenu> children; // For tree structure
}
