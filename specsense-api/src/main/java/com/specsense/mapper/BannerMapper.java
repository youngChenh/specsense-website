package com.specsense.mapper;

import com.specsense.model.entity.Banner;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BannerMapper {

    @Select("SELECT * FROM banner WHERE enabled = true AND locale = #{locale} ORDER BY sort_order")
    List<Banner> findEnabledByLocale(@Param("locale") String locale);

    @Select("SELECT * FROM banner ORDER BY sort_order")
    List<Banner> findAll();

    @Select("SELECT * FROM banner WHERE id = #{id}")
    Banner findById(@Param("id") Long id);

    @Insert("INSERT INTO banner (image_url, link_url, title_en, title_zh, subtitle_en, subtitle_zh, " +
            "sort_order, enabled, locale) " +
            "VALUES (#{imageUrl}, #{linkUrl}, #{titleEn}, #{titleZh}, #{subtitleEn}, #{subtitleZh}, " +
            "#{sortOrder}, #{enabled}, #{locale})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Banner banner);

    @Update("UPDATE banner SET image_url = #{imageUrl}, link_url = #{linkUrl}, " +
            "title_en = #{titleEn}, title_zh = #{titleZh}, " +
            "subtitle_en = #{subtitleEn}, subtitle_zh = #{subtitleZh}, " +
            "sort_order = #{sortOrder}, enabled = #{enabled}, locale = #{locale} " +
            "WHERE id = #{id}")
    int update(Banner banner);

    @Delete("DELETE FROM banner WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
