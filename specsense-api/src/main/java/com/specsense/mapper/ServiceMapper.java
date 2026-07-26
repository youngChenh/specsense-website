package com.specsense.mapper;

import com.specsense.model.entity.ServiceEntity;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ServiceMapper {

    @Select("SELECT * FROM service WHERE del_flag = 0 ORDER BY id")
    List<ServiceEntity> findAll();

    @Select("SELECT * FROM service WHERE del_flag = 0 AND id = #{id}")
    ServiceEntity findById(@Param("id") Long id);

    @Select("SELECT * FROM service WHERE del_flag = 0 AND `type` = #{type}")
    ServiceEntity findByType(@Param("type") String type);

    @Insert("INSERT INTO service (`type`, title_en, title_zh, description_en, description_zh, del_flag) " +
            "VALUES (#{type}, #{titleEn}, #{titleZh}, #{descriptionEn}, #{descriptionZh}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceEntity service);

    @Update("UPDATE service SET title_en = #{titleEn}, title_zh = #{titleZh}, " +
            "description_en = #{descriptionEn}, description_zh = #{descriptionZh} WHERE id = #{id}")
    int update(ServiceEntity service);

    @Update("UPDATE service SET del_flag = 1 WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
