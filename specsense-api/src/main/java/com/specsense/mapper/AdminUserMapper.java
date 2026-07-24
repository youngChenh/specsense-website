package com.specsense.mapper;

import com.specsense.model.entity.AdminUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminUserMapper {

    @Select("SELECT * FROM admin_user WHERE username = #{username}")
    AdminUser findByUsername(@Param("username") String username);

    @Select("SELECT * FROM admin_user WHERE id = #{id}")
    AdminUser findById(@Param("id") Long id);

    @Insert("INSERT INTO admin_user (username, password_hash, role) " +
            "VALUES (#{username}, #{passwordHash}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdminUser user);

    @Update("UPDATE admin_user SET password_hash = #{passwordHash}, role = #{role} WHERE id = #{id}")
    int update(AdminUser user);

    @Delete("DELETE FROM admin_user WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
