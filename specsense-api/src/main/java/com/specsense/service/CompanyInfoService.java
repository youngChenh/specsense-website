package com.specsense.service;

import com.specsense.model.entity.CompanyInfo;

public interface CompanyInfoService {
    CompanyInfo get(String locale);
    boolean update(CompanyInfo companyInfo);
}
