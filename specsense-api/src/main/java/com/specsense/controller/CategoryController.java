package com.specsense.controller;

import com.specsense.model.vo.Result;
import com.specsense.model.entity.Category;
import com.specsense.model.dto.CategoryDTO;
import com.specsense.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result<List<CategoryDTO>> getAll(@RequestParam(defaultValue = "en") String locale) {
        return Result.success(categoryService.getCategoryTree(locale));
    }

    @GetMapping("/list")
    public Result<List<Category>> getList() {
        return Result.success(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }
}
