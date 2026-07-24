package com.specsense.mapper;

import com.specsense.model.entity.Application;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ApplicationMapper {

    @Select("SELECT * FROM application ORDER BY id")
    List<Application> findAll();

    @Select("SELECT * FROM application WHERE id = #{id}")
    Application findById(@Param("id") Long id);

    @Select("SELECT * FROM application WHERE `type` = #{type}")
    Application findByType(@Param("type") String type);

    @Insert("INSERT INTO application (`type`, title_en, title_zh, description_en, description_zh, icon, image_url, linked_category_keys) " +
            "VALUES (#{type}, #{titleEn}, #{titleZh}, #{descriptionEn}, #{descriptionZh}, #{icon}, #{imageUrl}, #{linkedCategoryKeys})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Application application);

    @Update("UPDATE application SET `type` = #{type}, title_en = #{titleEn}, title_zh = #{titleZh}, " +
            "description_en = #{descriptionEn}, description_zh = #{descriptionZh}, " +
            "icon = #{icon}, image_url = #{imageUrl}, linked_category_keys = #{linkedCategoryKeys} WHERE id = #{id}")
    int update(Application application);

    @Delete("DELETE FROM application WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
