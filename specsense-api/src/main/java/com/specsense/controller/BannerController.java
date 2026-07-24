package com.specsense.controller;

import com.specsense.model.entity.Banner;
import com.specsense.model.vo.Result;
import com.specsense.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/banners")
    public Result<List<Banner>> getBanners(@RequestParam(defaultValue = "zh") String locale) {
        return Result.success(bannerService.getEnabledBanners(locale));
    }
}
