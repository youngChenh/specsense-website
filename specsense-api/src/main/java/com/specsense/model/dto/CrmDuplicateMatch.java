package com.specsense.model.dto;

import lombok.Data;

@Data
public class CrmDuplicateMatch {
    private Long id;
    private String name;
    private String country;
    private String website;
    private String reason;
}
