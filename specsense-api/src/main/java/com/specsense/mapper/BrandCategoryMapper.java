package com.specsense.mapper;

import com.specsense.model.entity.BrandCategory;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BrandCategoryMapper {

    @Select("SELECT * FROM brand_category ORDER BY sort_order")
    List<BrandCategory> findAll();

    @Select("SELECT * FROM brand_category ORDER BY sort_order")
    List<BrandCategory> findAllOrderBySort();

    @Select("SELECT * FROM brand_category WHERE id = #{id}")
    BrandCategory findById(@Param("id") Long id);

    @Select("SELECT * FROM brand_category WHERE `key` = #{key}")
    BrandCategory findByKey(@Param("key") String key);

    @Insert("INSERT INTO brand_category (name_en, name_zh, `key`, sort_order) " +
            "VALUES (#{nameEn}, #{nameZh}, #{key}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BrandCategory category);

    @Update("UPDATE brand_category SET name_en = #{nameEn}, name_zh = #{nameZh}, " +
            "`key` = #{key}, sort_order = #{sortOrder} WHERE id = #{id}")
    int update(BrandCategory category);

    @Delete("DELETE FROM brand_category WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM brand WHERE category_id = #{categoryId}")
    int countByCategoryId(@Param("categoryId") Long categoryId);
}
