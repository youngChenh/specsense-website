package com.specsense.service;

import com.specsense.model.entity.Category;
import com.specsense.model.dto.CategoryDTO;
import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getCategoryTree(String locale);
    List<Category> getAll();
    Category getById(Long id);
    Category getByKey(String key);
    boolean save(Category category);
    boolean update(Category category);
    boolean deleteById(Long id);
}
