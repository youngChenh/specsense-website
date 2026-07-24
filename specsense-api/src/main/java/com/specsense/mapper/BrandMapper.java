package com.specsense.mapper;

import com.specsense.model.entity.Brand;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BrandMapper {

    @Select("SELECT b.*, bc.name_en as category_name, bc.`key` as category_key " +
            "FROM brand b " +
            "LEFT JOIN brand_category bc ON b.category_id = bc.id " +
            "ORDER BY b.sort_order")
    List<Brand> findAll();

    @Select("<script>" +
            "SELECT b.*, bc.name_en as category_name, bc.`key` as category_key " +
            "FROM brand b " +
            "LEFT JOIN brand_category bc ON b.category_id = bc.id " +
            "WHERE 1=1 " +
            "<if test='categoryId != null'> AND b.category_id = #{categoryId}</if>" +
            "<if test='categoryKey != null'> AND bc.`key` = #{categoryKey}</if>" +
            "<if test='featured != null'> AND b.featured = #{featured}</if>" +
            "ORDER BY b.sort_order LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Brand> findList(@Param("categoryId") Long categoryId,
                         @Param("categoryKey") String categoryKey,
                         @Param("featured") Boolean featured,
                         @Param("offset") int offset,
                         @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM brand b " +
            "LEFT JOIN brand_category bc ON b.category_id = bc.id " +
            "WHERE 1=1 " +
            "<if test='categoryId != null'> AND b.category_id = #{categoryId}</if>" +
            "<if test='categoryKey != null'> AND bc.`key` = #{categoryKey}</if>" +
            "<if test='featured != null'> AND b.featured = #{featured}</if>" +
            "</script>")
    long count(@Param("categoryId") Long categoryId,
               @Param("categoryKey") String categoryKey,
               @Param("featured") Boolean featured);

    @Select("SELECT b.*, bc.name_en as category_name, bc.`key` as category_key " +
            "FROM brand b " +
            "LEFT JOIN brand_category bc ON b.category_id = bc.id " +
            "WHERE b.slug = #{slug}")
    Brand findBySlug(@Param("slug") String slug);

    @Select("SELECT * FROM brand WHERE id = #{id}")
    Brand findById(@Param("id") Long id);

    @Select("SELECT * FROM brand WHERE featured = true ORDER BY sort_order LIMIT #{limit}")
    List<Brand> findFeatured(@Param("limit") int limit);

    @Insert("INSERT INTO brand (category_id, name_en, name_zh, slug, logo_url, description_en, description_zh, " +
            "website_url, featured, sort_order) " +
            "VALUES (#{categoryId}, #{nameEn}, #{nameZh}, #{slug}, #{logoUrl}, #{descriptionEn}, #{descriptionZh}, " +
            "#{websiteUrl}, #{featured}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Brand brand);

    @Update("UPDATE brand SET category_id = #{categoryId}, name_en = #{nameEn}, name_zh = #{nameZh}, " +
            "slug = #{slug}, logo_url = #{logoUrl}, description_en = #{descriptionEn}, description_zh = #{descriptionZh}, " +
            "website_url = #{websiteUrl}, featured = #{featured}, sort_order = #{sortOrder} " +
            "WHERE id = #{id}")
    int update(Brand brand);

    @Delete("DELETE FROM brand WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
