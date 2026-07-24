package com.specsense.service.impl;

import com.specsense.mapper.BrandCategoryMapper;
import com.specsense.mapper.BrandMapper;
import com.specsense.model.entity.Brand;
import com.specsense.model.entity.BrandCategory;
import com.specsense.model.dto.BrandDTO;
import com.specsense.model.vo.PageResult;
import com.specsense.service.BrandService;
import com.specsense.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private BrandCategoryMapper brandCategoryMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    public PageResult<List<BrandDTO>> getList(Long categoryId, String categoryKey, Boolean featured,
                                               int page, int pageSize, String locale) {
        String listKey = String.format("brands:list:%d:%s:%s:%d:%d:%s",
            categoryId != null ? categoryId : 0,
            categoryKey != null ? categoryKey : "all",
            featured != null ? featured : "any",
            page, pageSize, locale);
        String totalKey = listKey + ":total";

        @SuppressWarnings("unchecked")
        List<BrandDTO> cachedList = cacheService.get(listKey, (Class<List<BrandDTO>>) (Class<?>) ArrayList.class);
        Integer cachedTotal = cacheService.get(totalKey, Integer.class);

        if (cachedList != null && cachedTotal != null) {
                return new PageResult<>(cachedTotal.longValue(), page, pageSize, cachedList);
        }

        int offset = (page - 1) * pageSize;
        long total = brandMapper.count(categoryId, categoryKey, featured);
        List<Brand> brands = brandMapper.findList(categoryId, categoryKey, featured, offset, pageSize);

        List<BrandDTO> dtos = new ArrayList<>();
        for (Brand brand : brands) {
            dtos.add(convertToDTO(brand, locale));
        }

        cacheService.set(listKey, dtos);
        cacheService.set(totalKey, total);
        return new PageResult<>(total, page, pageSize, dtos);
    }

    @Override
    public BrandDTO getBySlug(String slug, String locale) {
        Brand brand = brandMapper.findBySlug(slug);
        if (brand == null) {
            return null;
        }
        return convertToDTO(brand, locale);
    }

    @Override
    public List<BrandDTO> getFeatured(int limit, String locale) {
        String key = "brands:featured:" + locale;
        @SuppressWarnings("unchecked")
        List<BrandDTO> cached = cacheService.get(key, (Class<List<BrandDTO>>) (Class<?>) ArrayList.class);
        if (cached != null) {
            return cached;
        }
        List<Brand> brands = brandMapper.findFeatured(limit);
        List<BrandDTO> dtos = new ArrayList<>();
        for (Brand brand : brands) {
            dtos.add(convertToDTO(brand, locale));
        }
        cacheService.set(key, dtos);
        return dtos;
    }

    @Override
    public List<BrandDTO> getAll(String locale) {
        String key = CacheService.keyBrands(locale);
        @SuppressWarnings("unchecked")
        List<BrandDTO> cached = cacheService.get(key, (Class<List<BrandDTO>>) (Class<?>) ArrayList.class);
        if (cached != null) {
            return cached;
        }
        List<Brand> brands = brandMapper.findAll();
        List<BrandDTO> dtos = new ArrayList<>();
        for (Brand brand : brands) {
            dtos.add(convertToDTO(brand, locale));
        }
        cacheService.set(key, dtos);
        return dtos;
    }

    @Override
    public Brand getById(Long id) {
        return brandMapper.findById(id);
    }

    @Override
    public boolean save(Brand brand) {
        boolean result = brandMapper.insert(brand) > 0;
        if (result) {
            cacheService.deleteByPattern("brands:*");
        }
        return result;
    }

    @Override
    public boolean update(Brand brand) {
        boolean result = brandMapper.update(brand) > 0;
        if (result) {
            cacheService.deleteByPattern("brands:*");
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result = brandMapper.deleteById(id) > 0;
        if (result) {
            cacheService.deleteByPattern("brands:*");
        }
        return result;
    }

    private BrandDTO convertToDTO(Brand brand, String locale) {
        BrandDTO dto = new BrandDTO();
        dto.setId(brand.getId());
        dto.setCategoryId(brand.getCategoryId());
        dto.setNameEn(brand.getNameEn());
        dto.setNameZh(brand.getNameZh());
        dto.setSlug(brand.getSlug());
        dto.setLogoUrl(brand.getLogoUrl());
        dto.setDescriptionEn(brand.getDescriptionEn());
        dto.setDescriptionZh(brand.getDescriptionZh());
        dto.setWebsiteUrl(brand.getWebsiteUrl());
        dto.setFeatured(brand.getFeatured());
        dto.setSortOrder(brand.getSortOrder());
        dto.setCategoryName(brand.getCategoryName());
        dto.setCategoryKey(brand.getCategoryKey());
        return dto;
    }
}
