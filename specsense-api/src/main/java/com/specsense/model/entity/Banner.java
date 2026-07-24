package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Banner {
    private Long id;
    private String imageUrl;
    private String linkUrl;
    private String titleEn;
    private String titleZh;
    private String subtitleEn;
    private String subtitleZh;
    private Integer sortOrder;
    private Boolean enabled;
    private String locale;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For display
    private String title;
    private String subtitle;
}
