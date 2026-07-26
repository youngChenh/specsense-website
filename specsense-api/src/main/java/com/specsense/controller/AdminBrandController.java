package com.specsense.controller;

import com.specsense.config.JwtConfig;
import com.specsense.model.vo.Result;
import com.specsense.model.entity.Brand;
import com.specsense.model.entity.BrandCategory;
import com.specsense.model.dto.BrandDTO;
import com.specsense.service.BrandService;
import com.specsense.service.BrandCategoryService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminBrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandCategoryService brandCategoryService;

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

    // Brand Category CRUD
    @GetMapping("/brand-categories")
    public Result<List<BrandCategory>> listCategories(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(brandCategoryService.getAll());
    }

    @PostMapping("/brand-categories")
    public Result<Void> createCategory(@RequestBody BrandCategory category, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        brandCategoryService.save(category);
        return Result.success();
    }

    @PutMapping("/brand-categories/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody BrandCategory category, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        category.setId(id);
        brandCategoryService.update(category);
        return Result.success();
    }

    @DeleteMapping("/brand-categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        try {
            brandCategoryService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    // Brand CRUD
    @GetMapping("/brands")
    public Result<List<BrandDTO>> listBrands(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(brandService.getAll("en"));
    }

    @GetMapping("/brands/{id}")
    public Result<Brand> getBrand(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(brandService.getById(id));
    }

    @PostMapping("/brands")
    public Result<Void> createBrand(@RequestBody Brand brand, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        brandService.save(brand);
        return Result.success();
    }

    @PutMapping("/brands/{id}")
    public Result<Void> updateBrand(@PathVariable Long id, @RequestBody Brand brand, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        brand.setId(id);
        brandService.update(brand);
        return Result.success();
    }

    @DeleteMapping("/brands/{id}")
    public Result<Void> deleteBrand(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        brandService.deleteById(id);
        return Result.success();
    }
}
