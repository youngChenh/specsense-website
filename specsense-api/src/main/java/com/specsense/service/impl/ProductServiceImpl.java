package com.specsense.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.specsense.mapper.ProductMapper;
import com.specsense.model.entity.Product;
import com.specsense.model.dto.ProductDTO;
import com.specsense.model.dto.ProductSearchResultDTO;
import com.specsense.model.vo.PageResult;
import com.specsense.service.CacheService;
import com.specsense.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    public PageResult<List<ProductDTO>> getList(Long categoryId, String categoryKey, Boolean featured, String keyword, int page, int pageSize, String locale) {
        String normalizedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        // 只缓存数据列表，不缓存PageResult以避免泛型反序列化问题
        String listKey = String.format("products:list:%d:%s:%s:%s:%d:%d:%s",
            categoryId != null ? categoryId : 0,
            categoryKey != null ? categoryKey : "all",
            featured != null ? featured : "any",
            normalizedKeyword != null ? normalizedKeyword : "any",
            page, pageSize, locale);
        String totalKey = listKey + ":total";

        @SuppressWarnings("unchecked")
        List<ProductDTO> cachedList = cacheService.get(listKey, (Class<List<ProductDTO>>) (Class<?>) ArrayList.class);
        Integer cachedTotal = cacheService.get(totalKey, Integer.class);

        if (cachedList != null && cachedTotal != null) {
            return new PageResult<>(cachedTotal.longValue(), page, pageSize, cachedList);
        }

        int offset = (page - 1) * pageSize;
        long total = productMapper.count(categoryId, categoryKey, featured, normalizedKeyword);
        List<Product> products = productMapper.findList(categoryId, categoryKey, featured, normalizedKeyword, offset, pageSize);

        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            dtos.add(convertToDTO(product, locale));
        }

        cacheService.set(listKey, dtos);
        cacheService.set(totalKey, total);
        return new PageResult<>(total, page, pageSize, dtos);
    }

    @Override
    public ProductDTO getBySlug(String slug, String locale) {
        Product product = productMapper.findBySlug(slug);
        if (product == null) {
            return null;
        }
        return convertToDTO(product, locale);
    }

    @Override
    public ProductSearchResultDTO search(String keyword, String locale) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        String trimmed = keyword.trim();
        List<Product> top = productMapper.searchTop(trimmed, 5);
        if (top == null || top.isEmpty()) {
            ProductSearchResultDTO empty = new ProductSearchResultDTO();
            empty.setKeyword(trimmed);
            return empty;
        }
        Product best = top.get(0);
        ProductSearchResultDTO result = new ProductSearchResultDTO();
        result.setKeyword(trimmed);
        result.setCategoryKey(best.getCategoryKey());
        result.setCategoryName(best.getCategoryName());
        result.setBestMatch(convertToDTO(best, locale));
        return result;
    }

    @Override
    public Product getById(Long id) {
        Product cached = cacheService.get(CacheService.keyProduct(id), Product.class);
        if (cached != null) {
            return cached;
        }
        Product product = productMapper.findById(id);
        if (product != null) {
            cacheService.set(CacheService.keyProduct(id), product);
        }
        return product;
    }

    @Override
    public List<ProductDTO> getFeatured(int limit, String locale) {
        String key = CacheService.keyProductFeatured(locale);
        @SuppressWarnings("unchecked")
        List<ProductDTO> cached = cacheService.get(key, (Class<List<ProductDTO>>) (Class<?>) ArrayList.class);
        if (cached != null) {
            return cached;
        }

        List<Product> products = productMapper.findFeatured(limit);
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            dtos.add(convertToDTO(product, locale));
        }
        cacheService.set(key, dtos);
        return dtos;
    }

    @Override
    public boolean save(Product product) {
        normalizeSpecsJson(product);
        boolean result = productMapper.insert(product) > 0;
        if (result) {
            clearProductCache(product.getCategoryId());
        }
        return result;
    }

    @Override
    public boolean update(Product product) {
        normalizeSpecsJson(product);
        boolean result = productMapper.update(product) > 0;
        if (result) {
            cacheService.delete(CacheService.keyProduct(product.getId()));
            clearProductCache(product.getCategoryId());
        }
        return result;
    }

    private void normalizeSpecsJson(Product product) {
        if (product.getSpecsJson() == null || product.getSpecsJson().trim().isEmpty()) {
            product.setSpecsJson(null);
        }
        if (product.getImageUrls() == null || product.getImageUrls().trim().isEmpty()) {
            product.setImageUrls(null);
        }
        if (product.getPdfUrls() == null || product.getPdfUrls().trim().isEmpty()) {
            product.setPdfUrls(null);
        }
        if (product.getDownloadPdfUrl() == null || product.getDownloadPdfUrl().trim().isEmpty()) {
            product.setDownloadPdfUrl(null);
        }
        if (product.getDetailedSpecs() == null || product.getDetailedSpecs().trim().isEmpty()) {
            product.setDetailedSpecs(null);
        }
        if (product.getAlibabaImages() == null || product.getAlibabaImages().trim().isEmpty()) {
            product.setAlibabaImages(null);
        }
        if (product.getExternalImages() == null || product.getExternalImages().trim().isEmpty()) {
            product.setExternalImages(null);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Product product = productMapper.findById(id);
        boolean result = productMapper.deleteById(id) > 0;
        if (result && product != null) {
            cacheService.delete(CacheService.keyProduct(id));
            clearProductCache(product.getCategoryId());
        }
        return result;
    }

    private void clearProductCache(Long categoryId) {
        cacheService.deleteByPattern("products:*");
        cacheService.deleteByPattern("categories*");
    }

    private ProductDTO convertToDTO(Product product, String locale) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setCategoryId(product.getCategoryId());
        dto.setNameEn(product.getNameEn());
        dto.setNameZh(product.getNameZh());
        dto.setSlug(product.getSlug());
        dto.setDescriptionEn(product.getDescriptionEn());
        dto.setDescriptionZh(product.getDescriptionZh());
        dto.setImageUrl(product.getImageUrl());
        dto.setFeatured(product.getFeatured());
        dto.setSortOrder(product.getSortOrder());
        dto.setCategoryName(product.getCategoryName());
        dto.setCategoryKey(product.getCategoryKey());

        if ("zh".equals(locale)) {
            dto.setNameZh(product.getNameZh());
            dto.setDescriptionZh(product.getDescriptionZh());
            dto.setDetailDescZh(product.getDetailDescZh());
        } else {
            dto.setNameEn(product.getNameEn());
            dto.setDescriptionEn(product.getDescriptionEn());
            dto.setDetailDescEn(product.getDetailDescEn());
        }

        // Parse specs JSON
        if (product.getSpecsJson() != null && !product.getSpecsJson().isEmpty()) {
            try {
                Map<String, String> specs = objectMapper.readValue(
                    product.getSpecsJson(),
                    new TypeReference<Map<String, String>>() {}
                );
                dto.setSpecs(specs);
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        // Parse detailed specs JSON (independent from specs)
        if (product.getDetailedSpecs() != null && !product.getDetailedSpecs().isEmpty()) {
            try {
                Map<String, String> detailedSpecs = objectMapper.readValue(
                    product.getDetailedSpecs(),
                    new TypeReference<Map<String, String>>() {}
                );
                dto.setDetailedSpecs(detailedSpecs);
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        // Parse image URLs JSON array
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            try {
                List<String> imageUrls = objectMapper.readValue(
                    product.getImageUrls(),
                    new TypeReference<List<String>>() {}
                );
                dto.setImageUrls(imageUrls);
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        // Parse PDF URLs JSON array
        if (product.getPdfUrls() != null && !product.getPdfUrls().isEmpty()) {
            try {
                List<String> pdfUrls = objectMapper.readValue(
                    product.getPdfUrls(),
                    new TypeReference<List<String>>() {}
                );
                dto.setPdfUrls(pdfUrls);
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        // Single download PDF URL
        dto.setDownloadPdfUrl(product.getDownloadPdfUrl());

        // Parse alibaba images JSON array (max 10)
        if (product.getAlibabaImages() != null && !product.getAlibabaImages().isEmpty()) {
            try {
                List<String> alibabaImages = objectMapper.readValue(
                    product.getAlibabaImages(),
                    new TypeReference<List<String>>() {}
                );
                dto.setAlibabaImages(alibabaImages);
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        // Parse external images JSON array of {url, link}
        if (product.getExternalImages() != null && !product.getExternalImages().isEmpty()) {
            try {
                List<com.specsense.model.dto.ExternalImageItem> externalImages = objectMapper.readValue(
                    product.getExternalImages(),
                    new TypeReference<List<com.specsense.model.dto.ExternalImageItem>>() {}
                );
                dto.setExternalImages(externalImages);
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        // Set highlights and applications
        dto.setHighlights(product.getHighlights());
        dto.setApplications(product.getApplications());

        return dto;
    }
}
