package com.specsense.service;

import com.specsense.model.entity.Product;
import com.specsense.model.dto.ProductDTO;
import com.specsense.model.vo.PageResult;
import java.util.List;

public interface ProductService {
    PageResult<List<ProductDTO>> getList(Long categoryId, String categoryKey, Boolean featured, int page, int pageSize, String locale);
    ProductDTO getBySlug(String slug, String locale);
    Product getById(Long id);
    List<ProductDTO> getFeatured(int limit, String locale);
    boolean save(Product product);
    boolean update(Product product);
    boolean deleteById(Long id);
}
