package com.specsense.model.dto;

import lombok.Data;

@Data
public class BrandDTO {
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
    private String categoryName;
    private String categoryKey;
}
