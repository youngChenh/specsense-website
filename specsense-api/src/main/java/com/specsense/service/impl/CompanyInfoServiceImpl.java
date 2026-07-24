package com.specsense.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.specsense.mapper.CompanyInfoMapper;
import com.specsense.model.entity.CompanyInfo;
import com.specsense.service.CompanyInfoService;
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

    @Override
    public CompanyInfo get(String locale) {
        CompanyInfo info = companyInfoMapper.find();
        return info;
    }

    @Override
    public boolean update(CompanyInfo companyInfo) {
        return companyInfoMapper.update(companyInfo) > 0;
    }
}
