package com.specsense.mapper;

import com.specsense.model.entity.BrandCategory;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BrandCategoryMapper {

    @Select("SELECT * FROM brand_category WHERE del_flag = 0 ORDER BY sort_order")
    List<BrandCategory> findAll();

    @Select("SELECT * FROM brand_category WHERE del_flag = 0 ORDER BY sort_order")
    List<BrandCategory> findAllOrderBySort();

    @Select("SELECT * FROM brand_category WHERE del_flag = 0 AND id = #{id}")
    BrandCategory findById(@Param("id") Long id);

    @Select("SELECT * FROM brand_category WHERE del_flag = 0 AND `key` = #{key}")
    BrandCategory findByKey(@Param("key") String key);

    @Insert("INSERT INTO brand_category (name_en, name_zh, `key`, sort_order, del_flag) " +
            "VALUES (#{nameEn}, #{nameZh}, #{key}, #{sortOrder}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BrandCategory category);

    @Update("UPDATE brand_category SET name_en = #{nameEn}, name_zh = #{nameZh}, " +
            "`key` = #{key}, sort_order = #{sortOrder} WHERE id = #{id}")
    int update(BrandCategory category);

    @Update("UPDATE brand_category SET del_flag = 1 WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM brand WHERE del_flag = 0 AND category_id = #{categoryId}")
    int countByCategoryId(@Param("categoryId") Long categoryId);
}
