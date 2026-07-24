package com.specsense.model.dto;

import lombok.Data;

@Data
public class ApplicationDTO {
    private Long id;
    private String type;
    private String titleEn;
    private String titleZh;
    private String descriptionEn;
    private String descriptionZh;
    private String icon;
    private Object imageUrl;
    private String linkedCategoryKeys;
}
