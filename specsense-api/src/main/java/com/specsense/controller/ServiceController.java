package com.specsense.controller;

import com.specsense.model.vo.Result;
import com.specsense.model.entity.ServiceEntity;
import com.specsense.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public Result<List<ServiceEntity>> getAll(@RequestParam(defaultValue = "en") String locale) {
        return Result.success(serviceService.getAll(locale));
    }
}
