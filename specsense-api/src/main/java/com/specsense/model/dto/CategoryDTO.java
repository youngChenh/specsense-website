package com.specsense.model.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CategoryDTO {
    private Long id;
    private String nameEn;
    private String nameZh;
    private String key;
    private Long parentId;
    private Integer sortOrder;
    private List<CategoryDTO> children;
    private Map<String, Object> specs;  // For specs_json display
}
