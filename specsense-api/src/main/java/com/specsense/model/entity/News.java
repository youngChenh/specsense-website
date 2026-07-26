package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class News {
    private Long id;
    private String category;
    private String titleEn;
    private String titleZh;
    private String slug;
    private String excerptEn;
    private String excerptZh;
    private String contentEn;
    private String contentZh;
    private String imageUrl;
    private String author;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer delFlag;

    // For display
    private String title;
    private String excerpt;
    private String content;
    private String categoryLabel;
}
