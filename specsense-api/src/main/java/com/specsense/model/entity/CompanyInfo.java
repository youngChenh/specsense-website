package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyInfo {
    private Long id;
    private String imageUrl;
    private String aboutEn;
    private String aboutZh;
    private String descriptionEn;
    private String descriptionZh;
    private String missionEn;
    private String missionZh;
    private String email;
    private String phone;
    private String addressEn;
    private String addressZh;
    private String valuesJson;
    private String timelineJson;
    private String qualificationsJson;
    private String partnersStatsJson;
    private String statsJson;
    private LocalDateTime updatedAt;
}
