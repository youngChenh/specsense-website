package com.specsense.mapper;

import com.specsense.model.entity.NavigationMenu;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface NavigationMenuMapper {

    @Select("SELECT * FROM navigation_menu WHERE del_flag = 0 AND position = #{position} ORDER BY sort_order")
    List<NavigationMenu> findByPosition(@Param("position") String position);

    @Select("SELECT * FROM navigation_menu WHERE del_flag = 0 ORDER BY position, sort_order")
    List<NavigationMenu> findAll();

    @Select("SELECT * FROM navigation_menu WHERE del_flag = 0 AND id = #{id}")
    NavigationMenu findById(@Param("id") Long id);

    @Insert("INSERT INTO navigation_menu (`key`, position, label_en, label_zh, path, icon, sort_order, parent_id, enabled, del_flag) " +
            "VALUES (#{key}, #{position}, #{labelEn}, #{labelZh}, #{path}, #{icon}, #{sortOrder}, #{parentId}, #{enabled,jdbcType=BIT}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NavigationMenu menu);

    @Update("UPDATE navigation_menu SET `key` = #{key}, position = #{position}, label_en = #{labelEn}, " +
            "label_zh = #{labelZh}, path = #{path}, icon = #{icon}, sort_order = #{sortOrder}, " +
            "parent_id = #{parentId}, enabled = #{enabled,jdbcType=BIT} WHERE id = #{id}")
    int update(NavigationMenu menu);

    @Update("UPDATE navigation_menu SET del_flag = 1 WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
