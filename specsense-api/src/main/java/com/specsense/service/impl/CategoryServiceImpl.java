package com.specsense.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.specsense.mapper.CategoryMapper;
import com.specsense.mapper.ProductMapper;
import com.specsense.model.entity.Category;
import com.specsense.model.dto.CategoryDTO;
import com.specsense.service.CacheService;
import com.specsense.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    public List<CategoryDTO> getCategoryTree(String locale) {
        String key = "categories:tree:" + locale;
        @SuppressWarnings("unchecked")
        List<CategoryDTO> cached = cacheService.get(key, (Class<List<CategoryDTO>>) (Class<?>) ArrayList.class);
        if (cached != null) {
            return cached;
        }

        List<Category> rootCategories = categoryMapper.findRootCategories();
        List<CategoryDTO> result = new ArrayList<>();

        for (Category root : rootCategories) {
            CategoryDTO dto = convertToDTO(root, locale);
            List<Category> children = categoryMapper.findByParentId(root.getId());
            dto.setChildren(convertChildren(children, locale));
            result.add(dto);
        }

        cacheService.set(key, result);
        return result;
    }

    private List<CategoryDTO> convertChildren(List<Category> categories, String locale) {
        List<CategoryDTO> result = new ArrayList<>();
        for (Category category : categories) {
            CategoryDTO dto = convertToDTO(category, locale);
            List<Category> children = categoryMapper.findByParentId(category.getId());
            if (!children.isEmpty()) {
                dto.setChildren(convertChildren(children, locale));
            }
            result.add(dto);
        }
        return result;
    }

    private CategoryDTO convertToDTO(Category category, String locale) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setNameEn(category.getNameEn());
        dto.setNameZh(category.getNameZh());
        dto.setKey(category.getKey());
        dto.setParentId(category.getParentId());
        dto.setSortOrder(category.getSortOrder());

        if ("zh".equals(locale)) {
            dto.setNameZh(category.getNameZh());
        } else {
            dto.setNameEn(category.getNameEn());
        }

        return dto;
    }

    @Override
    public List<Category> getAll() {
        return categoryMapper.findAll();
    }

    @Override
    public Category getById(Long id) {
        return categoryMapper.findById(id);
    }

    @Override
    public Category getByKey(String key) {
        return categoryMapper.findByKey(key);
    }

    @Override
    public boolean save(Category category) {
        boolean result = categoryMapper.insert(category) > 0;
        if (result) {
            cacheService.deleteByPattern("categories:*");
        }
        return result;
    }

    @Override
    public boolean update(Category category) {
        boolean result = categoryMapper.update(category) > 0;
        if (result) {
            cacheService.deleteByPattern("categories:*");
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        if (categoryMapper.countByParentId(id) > 0) {
            throw new RuntimeException("Cannot delete category with children");
        }
        if (productMapper.countByCategoryId(id) > 0) {
            throw new RuntimeException("Cannot delete category with products");
        }
        boolean result = categoryMapper.deleteById(id) > 0;
        if (result) {
            cacheService.deleteByPattern("categories:*");
        }
        return result;
    }
}
