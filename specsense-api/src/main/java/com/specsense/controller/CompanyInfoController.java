package com.specsense.controller;

import com.specsense.model.vo.Result;
import com.specsense.model.entity.CompanyInfo;
import com.specsense.service.CompanyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company-info")
public class CompanyInfoController {

    @Autowired
    private CompanyInfoService companyInfoService;

    @GetMapping
    public Result<CompanyInfo> get(@RequestParam(defaultValue = "en") String locale) {
        return Result.success(companyInfoService.get(locale));
    }
}
