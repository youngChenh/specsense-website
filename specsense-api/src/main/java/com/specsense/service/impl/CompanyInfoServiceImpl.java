package com.specsense.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.specsense.mapper.CompanyInfoMapper;
import com.specsense.model.entity.CompanyInfo;
import com.specsense.service.CompanyInfoService;
import com.specsense.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CompanyInfoServiceImpl implements CompanyInfoService {

    @Autowired
    private CompanyInfoMapper companyInfoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    public CompanyInfo get(String locale) {
        String key = CacheService.keyCompany();
        CompanyInfo cached = cacheService.get(key, CompanyInfo.class);
        if (cached != null) {
            return cached;
        }
        CompanyInfo info = companyInfoMapper.find();
        if (info != null) {
            cacheService.set(key, info);
        }
        return info;
    }

    @Override
    public boolean update(CompanyInfo companyInfo) {
        boolean result = companyInfoMapper.update(companyInfo) > 0;
        if (result) {
            cacheService.delete(CacheService.keyCompany());
        }
        return result;
    }
}
