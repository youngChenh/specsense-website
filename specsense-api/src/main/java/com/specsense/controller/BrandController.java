package com.specsense.controller;

import com.specsense.model.vo.Result;
import com.specsense.model.vo.PageResult;
import com.specsense.model.dto.BrandCategoryDTO;
import com.specsense.model.dto.BrandDTO;
import com.specsense.service.BrandCategoryService;
import com.specsense.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BrandController {

    @Autowired
    private BrandCategoryService brandCategoryService;

    @Autowired
    private BrandService brandService;

    @GetMapping("/brands/categories")
    public Result<List<BrandCategoryDTO>> getCategories(@RequestParam(defaultValue = "en") String locale) {
        return Result.success(brandCategoryService.getCategoryTree(locale));
    }

    @GetMapping("/brands")
    public Result<PageResult<List<BrandDTO>>> getBrands(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(defaultValue = "en") String locale) {
        return Result.success(brandService.getList(categoryId, category, featured, page, pageSize, locale));
    }

    @GetMapping("/brands/featured")
    public Result<List<BrandDTO>> getFeatured(
            @RequestParam(defaultValue = "8") int limit,
            @RequestParam(defaultValue = "en") String locale) {
        return Result.success(brandService.getFeatured(limit, locale));
    }

    @GetMapping("/brands/{slug}")
    public Result<BrandDTO> getBySlug(@PathVariable String slug,
                                       @RequestParam(defaultValue = "en") String locale) {
        BrandDTO brand = brandService.getBySlug(slug, locale);
        if (brand == null) {
            return Result.error(404, "Brand not found");
        }
        return Result.success(brand);
    }
}
