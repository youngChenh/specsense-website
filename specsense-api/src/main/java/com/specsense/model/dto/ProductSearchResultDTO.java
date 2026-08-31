package com.specsense.model.dto;

import lombok.Data;

@Data
public class ProductSearchResultDTO {
    private String keyword;
    private String categoryKey;
    private String categoryName;
    private ProductDTO bestMatch;
}
