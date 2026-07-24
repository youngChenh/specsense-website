package com.specsense.mapper;

import com.specsense.model.entity.News;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface NewsMapper {

    @Select("<script>" +
            "SELECT * FROM news WHERE 1=1 " +
            "<if test='category != null'> AND category = #{category}</if>" +
            "ORDER BY published_at DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<News> findList(@Param("category") String category,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM news WHERE 1=1 " +
            "<if test='category != null'> AND category = #{category}</if>" +
            "</script>")
    long count(@Param("category") String category);

    @Select("SELECT * FROM news WHERE slug = #{slug}")
    News findBySlug(@Param("slug") String slug);

    @Select("SELECT * FROM news WHERE id = #{id}")
    News findById(@Param("id") Long id);

    @Select("SELECT * FROM news ORDER BY published_at DESC LIMIT #{limit}")
    List<News> findLatest(@Param("limit") int limit);

    @Insert("INSERT INTO news (category, title_en, title_zh, slug, excerpt_en, excerpt_zh, " +
            "content_en, content_zh, image_url, author, published_at) " +
            "VALUES (#{category}, #{titleEn}, #{titleZh}, #{slug}, #{excerptEn}, #{excerptZh}, " +
            "#{contentEn}, #{contentZh}, #{imageUrl}, #{author}, #{publishedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(News news);

    @Update("UPDATE news SET category = #{category}, title_en = #{titleEn}, title_zh = #{titleZh}, " +
            "slug = #{slug}, excerpt_en = #{excerptEn}, excerpt_zh = #{excerptZh}, " +
            "content_en = #{contentEn}, content_zh = #{contentZh}, image_url = #{imageUrl}, " +
            "author = #{author}, published_at = #{publishedAt} WHERE id = #{id}")
    int update(News news);

    @Delete("DELETE FROM news WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
