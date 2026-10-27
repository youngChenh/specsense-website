package com.specsense.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CrmCustomer {
    private Long id;
    private String name;
    private String country;
    private String website;
    private String industry;
    private String address;
    private String source;
    private Long ownerId;
    private String ownerName;
    private String status;
    private String remark;
    private String attachments;
    private Integer closed;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Integer contactCount;
    private Integer inquiryCount;
}
