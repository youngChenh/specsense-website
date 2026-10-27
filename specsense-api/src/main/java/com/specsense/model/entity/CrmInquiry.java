package com.specsense.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CrmInquiry {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long contactId;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate inquiryDate;
    private String source;
    private String requirement;
    private String stage;
    private Long ownerId;
    private String ownerName;
    private Long websiteInquiryId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextFollowDate;
    private String remark;
    private String attachments;
    private Integer closed;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
