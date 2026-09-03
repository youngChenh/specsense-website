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
            "WHERE p.del_flag = 0 " +
            "<if test='categoryId != null'> AND p.category_id = #{categoryId}</if>" +
            "<if test='categoryKey != null'> " +
            " AND (c.`key` = #{categoryKey} OR c.parent_id IN (SELECT id FROM category WHERE `key` = #{categoryKey}))" +
            "</if>" +
            "<if test='featured != null'> AND p.featured = #{featured}</if>" +
            "<if test='keyword != null and keyword != \"\"'> " +
            " AND (p.name_en LIKE CONCAT('%', #{keyword}, '%') OR p.name_zh LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<choose>" +
            "<when test='keyword != null and keyword != \"\"'>" +
            " ORDER BY " +
            "   CASE WHEN p.name_en = #{keyword} OR p.name_zh = #{keyword} THEN 0 " +
            "        WHEN p.name_en LIKE CONCAT(#{keyword}, '%') OR p.name_zh LIKE CONCAT(#{keyword}, '%') THEN 1 " +
            "        ELSE 2 END, " +
            "   p.sort_order" +
            "</when>" +
            "<otherwise>ORDER BY p.sort_order</otherwise>" +
            "</choose>" +
            " LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Product> findList(@Param("categoryId") Long categoryId,
                           @Param("categoryKey") String categoryKey,
                           @Param("featured") Boolean featured,
                           @Param("keyword") String keyword,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    @Select("<script>" +
            "SELECT p.*, c.name_en as category_name, c.`key` as category_key, " +
            "       CASE WHEN p.name_en = #{keyword} OR p.name_zh = #{keyword} THEN 0 " +
            "            WHEN p.name_en LIKE CONCAT(#{keyword}, '%') OR p.name_zh LIKE CONCAT(#{keyword}, '%') THEN 1 " +
            "            ELSE 2 END AS match_rank " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.del_flag = 0 " +
            "  AND (p.name_en LIKE CONCAT('%', #{keyword}, '%') OR p.name_zh LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY match_rank, p.sort_order " +
            "LIMIT #{limit}" +
            "</script>")
    List<Product> searchTop(@Param("keyword") String keyword,
                            @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.del_flag = 0 " +
            "<if test='categoryId != null'> AND p.category_id = #{categoryId}</if>" +
            "<if test='categoryKey != null'> " +
            " AND (c.`key` = #{categoryKey} OR c.parent_id IN (SELECT id FROM category WHERE `key` = #{categoryKey}))" +
            "</if>" +
            "<if test='featured != null'> AND p.featured = #{featured}</if>" +
            "<if test='keyword != null and keyword != \"\"'> " +
            " AND (p.name_en LIKE CONCAT('%', #{keyword}, '%') OR p.name_zh LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "</script>")
    long count(@Param("categoryId") Long categoryId,
              @Param("categoryKey") String categoryKey,
              @Param("featured") Boolean featured,
              @Param("keyword") String keyword);

    @Select("SELECT p.*, c.name_en as category_name, c.`key` as category_key " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.del_flag = 0 AND p.slug = #{slug}")
    Product findBySlug(@Param("slug") String slug);

    @Select("SELECT * FROM product WHERE del_flag = 0 AND id = #{id}")
    Product findById(@Param("id") Long id);

    @Select("SELECT * FROM product WHERE del_flag = 0 AND featured = true ORDER BY sort_order LIMIT #{limit}")
    List<Product> findFeatured(@Param("limit") int limit);

    @Insert("INSERT INTO product (category_id, name_en, name_zh, slug, description_en, description_zh, " +
            "image_url, image_urls, download_pdf_url, specs_json, overview_modules, " +
            "featured, sort_order, del_flag, highlights, applications) " +
            "VALUES (#{categoryId}, #{nameEn}, #{nameZh}, #{slug}, #{descriptionEn}, #{descriptionZh}, " +
            "#{imageUrl}, #{imageUrls}, #{downloadPdfUrl}, #{specsJson}, #{overviewModules}, " +
            "#{featured}, #{sortOrder}, 0, #{highlights}, #{applications})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE product SET category_id = #{categoryId}, name_en = #{nameEn}, name_zh = #{nameZh}, " +
            "slug = #{slug}, description_en = #{descriptionEn}, description_zh = #{descriptionZh}, " +
            "image_url = #{imageUrl}, image_urls = #{imageUrls}, download_pdf_url = #{downloadPdfUrl}, " +
            "specs_json = #{specsJson}, overview_modules = #{overviewModules}, " +
            "featured = #{featured}, sort_order = #{sortOrder}, " +
            "highlights = #{highlights}, applications = #{applications} " +
            "WHERE id = #{id}")
    int update(Product product);

    @Update("UPDATE product SET del_flag = 1 WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM product WHERE del_flag = 0 AND category_id = #{categoryId}")
    int countByCategoryId(@Param("categoryId") Long categoryId);
}
