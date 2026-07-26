package com.specsense.mapper;

import com.specsense.model.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM category WHERE del_flag = 0 ORDER BY sort_order")
    List<Category> findAll();

    @Select("SELECT * FROM category WHERE del_flag = 0 AND parent_id IS NULL ORDER BY sort_order")
    List<Category> findRootCategories();

    @Select("SELECT * FROM category WHERE del_flag = 0 AND parent_id = #{parentId} ORDER BY sort_order")
    List<Category> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM category WHERE del_flag = 0 AND id = #{id}")
    Category findById(@Param("id") Long id);

    @Select("SELECT * FROM category WHERE del_flag = 0 AND `key` = #{key}")
    Category findByKey(@Param("key") String key);

    @Insert("INSERT INTO category (name_en, name_zh, `key`, parent_id, sort_order, del_flag) " +
            "VALUES (#{nameEn}, #{nameZh}, #{key}, #{parentId}, #{sortOrder}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    @Update("UPDATE category SET name_en = #{nameEn}, name_zh = #{nameZh}, " +
            "`key` = #{key}, parent_id = #{parentId}, sort_order = #{sortOrder} WHERE id = #{id}")
    int update(Category category);

    @Update("UPDATE category SET del_flag = 1 WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM category WHERE del_flag = 0 AND parent_id = #{parentId}")
    int countByParentId(@Param("parentId") Long parentId);
}
