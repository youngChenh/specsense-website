package com.specsense.service;

import com.specsense.model.entity.Brand;
import com.specsense.model.dto.BrandDTO;
import com.specsense.model.vo.PageResult;
import java.util.List;

public interface BrandService {
    PageResult<List<BrandDTO>> getList(Long categoryId, String categoryKey, Boolean featured, int page, int pageSize, String locale);
    BrandDTO getBySlug(String slug, String locale);
    List<BrandDTO> getFeatured(int limit, String locale);
    List<BrandDTO> getAll(String locale);
    Brand getById(Long id);
    boolean save(Brand brand);
    boolean update(Brand brand);
    boolean deleteById(Long id);
}
