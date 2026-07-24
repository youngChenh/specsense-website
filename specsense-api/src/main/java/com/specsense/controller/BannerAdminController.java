package com.specsense.controller;

import com.specsense.config.JwtConfig;
import com.specsense.model.vo.Result;
import com.specsense.model.entity.Banner;
import com.specsense.service.BannerService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class BannerAdminController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private JwtConfig jwtConfig;

    private boolean authenticate(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtConfig.parseToken(token);
            return claims != null;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/banners")
    public Result<List<Banner>> listBanners(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(bannerService.getAllBanners());
    }

    @GetMapping("/banners/{id}")
    public Result<Banner> getBanner(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(bannerService.getById(id));
    }

    @PostMapping("/banners")
    public Result<Void> createBanner(@RequestBody Banner banner, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        bannerService.save(banner);
        return Result.success();
    }

    @PutMapping("/banners/{id}")
    public Result<Void> updateBanner(@PathVariable Long id, @RequestBody Banner banner, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        banner.setId(id);
        bannerService.update(banner);
        return Result.success();
    }

    @DeleteMapping("/banners/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        bannerService.deleteById(id);
        return Result.success();
    }
}
