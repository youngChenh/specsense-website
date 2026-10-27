package com.specsense.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CrmImportRow {
    private int rowNum;
    private String name;
    private String country;
    private String website;
    private String industry;
    private String address;
    private String source;
    private String remark;
    private String contactName;
    private String contactTitle;
    private String contactEmail;
    private String contactPhone;
    private List<String> errors = new ArrayList<String>();
    private List<CrmDuplicateMatch> duplicates = new ArrayList<CrmDuplicateMatch>();
    /** skip | merge | create */
    private String action = "create";
    private Long mergeTargetId;
}
