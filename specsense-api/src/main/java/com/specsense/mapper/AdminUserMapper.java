package com.specsense.mapper;

import com.specsense.model.entity.AdminUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminUserMapper {

    @Select("SELECT * FROM admin_user WHERE username = #{username}")
    AdminUser findByUsername(@Param("username") String username);

    @Select("SELECT * FROM admin_user WHERE id = #{id}")
    AdminUser findById(@Param("id") Long id);

    @Select("SELECT * FROM admin_user ORDER BY id DESC")
    List<AdminUser> findAll();

    @Select("SELECT * FROM admin_user WHERE status = 1 ORDER BY id DESC")
    List<AdminUser> findActive();

    @Insert("INSERT INTO admin_user (username, password_hash, role, display_name, status, reset_requested) " +
            "VALUES (#{username}, #{passwordHash}, #{role}, #{displayName}, #{status}, #{resetRequested})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdminUser user);

    @Update("UPDATE admin_user SET display_name = #{displayName}, role = #{role}, status = #{status} WHERE id = #{id}")
    int updateProfile(AdminUser user);

    @Update("UPDATE admin_user SET password_hash = #{passwordHash}, role = #{role} WHERE id = #{id}")
    int update(AdminUser user);

    @Update("UPDATE admin_user SET password_hash = #{passwordHash}, reset_requested = 0 WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);

    @Update("UPDATE admin_user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE admin_user SET reset_requested = #{resetRequested} WHERE id = #{id}")
    int updateResetRequested(@Param("id") Long id, @Param("resetRequested") Integer resetRequested);

    @Delete("DELETE FROM admin_user WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
