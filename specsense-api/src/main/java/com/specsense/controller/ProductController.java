package com.specsense.controller;

import com.specsense.model.vo.Result;
import com.specsense.model.vo.PageResult;
import com.specsense.model.dto.ProductDTO;
import com.specsense.model.entity.Product;
import com.specsense.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Result<PageResult<List<ProductDTO>>> getList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(defaultValue = "en") String locale) {
        return Result.success(productService.getList(categoryId, category, featured, page, pageSize, locale));
    }

    @GetMapping("/{slug}")
    public Result<ProductDTO> getBySlug(@PathVariable String slug,
                                        @RequestParam(defaultValue = "en") String locale) {
        ProductDTO product = productService.getBySlug(slug, locale);
        if (product == null) {
            return Result.error(404, "Product not found");
        }
        return Result.success(product);
    }

    @GetMapping("/featured")
    public Result<List<ProductDTO>> getFeatured(
            @RequestParam(defaultValue = "4") int limit,
            @RequestParam(defaultValue = "en") String locale) {
        return Result.success(productService.getFeatured(limit, locale));
    }
}
