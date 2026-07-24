package com.specsense.mapper;

import com.specsense.model.entity.Download;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DownloadMapper {

    @Select("<script>" +
            "SELECT * FROM download WHERE 1=1 " +
            "<if test='category != null'> AND category = #{category}</if>" +
            "ORDER BY created_at DESC" +
            "</script>")
    List<Download> findAll(@Param("category") String category);

    @Select("SELECT * FROM download WHERE id = #{id}")
    Download findById(@Param("id") Long id);

    @Insert("INSERT INTO download (category, name_en, name_zh, description_en, description_zh, file_url, file_size, original_filename) " +
            "VALUES (#{category}, #{nameEn}, #{nameZh}, #{descriptionEn}, #{descriptionZh}, #{fileUrl}, #{fileSize}, #{originalFilename})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Download download);

    @Update("UPDATE download SET category = #{category}, name_en = #{nameEn}, name_zh = #{nameZh}, " +
            "description_en = #{descriptionEn}, description_zh = #{descriptionZh}, " +
            "file_url = #{fileUrl}, file_size = #{fileSize}, original_filename = #{originalFilename} WHERE id = #{id}")
    int update(Download download);

    @Delete("DELETE FROM download WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
