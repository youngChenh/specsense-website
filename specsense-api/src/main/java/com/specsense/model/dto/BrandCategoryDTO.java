package com.specsense.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class BrandCategoryDTO {
    private Long id;
    private String nameEn;
    private String nameZh;
    private String key;
    private Integer sortOrder;
    private List<BrandDTO> brands;
}
