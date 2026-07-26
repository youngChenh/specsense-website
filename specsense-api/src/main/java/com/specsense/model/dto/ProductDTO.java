package com.specsense.model.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ProductDTO {
    private Long id;
    private Long categoryId;
    private String nameEn;
    private String nameZh;
    private String slug;
    private String descriptionEn;
    private String descriptionZh;
    private String imageUrl;
    private List<String> imageUrls;
    private List<String> pdfUrls;
    private Map<String, String> specs;
    private Boolean featured;
    private Integer sortOrder;
    private String categoryName;
    private String categoryKey;
    private String highlights;
    private String applications;
}
