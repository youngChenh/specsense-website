package com.specsense.mapper;

import com.specsense.model.entity.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("<script>" +
            "SELECT p.*, c.name_en as category_name, c.`key` as category_key " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE 1=1 " +
            "<if test='categoryId != null'> AND p.category_id = #{categoryId}</if>" +
            "<if test='categoryKey != null'> " +
            " AND (c.`key` = #{categoryKey} OR c.parent_id IN (SELECT id FROM category WHERE `key` = #{categoryKey}))" +
            "</if>" +
            "<if test='featured != null'> AND p.featured = #{featured}</if>" +
            "ORDER BY p.sort_order LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Product> findList(@Param("categoryId") Long categoryId,
                           @Param("categoryKey") String categoryKey,
                           @Param("featured") Boolean featured,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE 1=1 " +
            "<if test='categoryId != null'> AND p.category_id = #{categoryId}</if>" +
            "<if test='categoryKey != null'> " +
            " AND (c.`key` = #{categoryKey} OR c.parent_id IN (SELECT id FROM category WHERE `key` = #{categoryKey}))" +
            "</if>" +
            "<if test='featured != null'> AND p.featured = #{featured}</if>" +
            "</script>")
    long count(@Param("categoryId") Long categoryId,
              @Param("categoryKey") String categoryKey,
              @Param("featured") Boolean featured);

    @Select("SELECT p.*, c.name_en as category_name, c.`key` as category_key " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.slug = #{slug}")
    Product findBySlug(@Param("slug") String slug);

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(@Param("id") Long id);

    @Select("SELECT * FROM product WHERE featured = true ORDER BY sort_order LIMIT #{limit}")
    List<Product> findFeatured(@Param("limit") int limit);

    @Insert("INSERT INTO product (category_id, name_en, name_zh, slug, description_en, description_zh, " +
            "image_url, image_urls, pdf_urls, specs_json, featured, sort_order) " +
            "VALUES (#{categoryId}, #{nameEn}, #{nameZh}, #{slug}, #{descriptionEn}, #{descriptionZh}, " +
            "#{imageUrl}, #{imageUrls}, #{pdfUrls}, #{specsJson}, #{featured}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE product SET category_id = #{categoryId}, name_en = #{nameEn}, name_zh = #{nameZh}, " +
            "slug = #{slug}, description_en = #{descriptionEn}, description_zh = #{descriptionZh}, " +
            "image_url = #{imageUrl}, image_urls = #{imageUrls}, pdf_urls = #{pdfUrls}, " +
            "specs_json = #{specsJson}, featured = #{featured}, sort_order = #{sortOrder} " +
            "WHERE id = #{id}")
    int update(Product product);

    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
