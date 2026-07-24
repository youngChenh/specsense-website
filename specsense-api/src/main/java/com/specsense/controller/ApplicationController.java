package com.specsense.controller;

import com.specsense.model.vo.Result;
import com.specsense.model.entity.Application;
import com.specsense.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping
    public Result<List<Application>> getAll(@RequestParam(defaultValue = "en") String locale) {
        return Result.success(applicationService.getAll(locale));
    }
}
