package com.specsense.service.impl;

import com.specsense.mapper.BrandCategoryMapper;
import com.specsense.mapper.BrandMapper;
import com.specsense.model.entity.BrandCategory;
import com.specsense.model.entity.Brand;
import com.specsense.model.dto.BrandCategoryDTO;
import com.specsense.model.dto.BrandDTO;
import com.specsense.service.BrandCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrandCategoryServiceImpl implements BrandCategoryService {

    @Autowired
    private BrandCategoryMapper brandCategoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<BrandCategoryDTO> getCategoryTree(String locale) {
        List<BrandCategory> categories = brandCategoryMapper.findAllOrderBySort();
        List<BrandCategoryDTO> result = new ArrayList<>();

        for (BrandCategory category : categories) {
            BrandCategoryDTO dto = convertToDTO(category, locale);
            result.add(dto);
        }

        return result;
    }

    private BrandCategoryDTO convertToDTO(BrandCategory category, String locale) {
        BrandCategoryDTO dto = new BrandCategoryDTO();
        dto.setId(category.getId());
        dto.setNameEn(category.getNameEn());
        dto.setNameZh(category.getNameZh());
        dto.setKey(category.getKey());
        dto.setSortOrder(category.getSortOrder());
        return dto;
    }

    @Override
    public List<BrandCategory> getAll() {
        return brandCategoryMapper.findAll();
    }

    @Override
    public BrandCategory getById(Long id) {
        return brandCategoryMapper.findById(id);
    }

    @Override
    public BrandCategory getByKey(String key) {
        return brandCategoryMapper.findByKey(key);
    }

    @Override
    public boolean save(BrandCategory category) {
        return brandCategoryMapper.insert(category) > 0;
    }

    @Override
    public boolean update(BrandCategory category) {
        return brandCategoryMapper.update(category) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        if (brandCategoryMapper.countByCategoryId(id) > 0) {
            throw new RuntimeException("Cannot delete category with brands");
        }
        return brandCategoryMapper.deleteById(id) > 0;
    }
}
