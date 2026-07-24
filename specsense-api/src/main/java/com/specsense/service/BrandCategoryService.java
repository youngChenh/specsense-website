package com.specsense.service;

import com.specsense.model.entity.BrandCategory;
import com.specsense.model.dto.BrandCategoryDTO;
import java.util.List;

public interface BrandCategoryService {
    List<BrandCategoryDTO> getCategoryTree(String locale);
    List<BrandCategory> getAll();
    BrandCategory getById(Long id);
    BrandCategory getByKey(String key);
    boolean save(BrandCategory category);
    boolean update(BrandCategory category);
    boolean deleteById(Long id);
}
