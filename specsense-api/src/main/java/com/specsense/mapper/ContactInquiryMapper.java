package com.specsense.mapper;

import com.specsense.model.entity.ContactInquiry;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ContactInquiryMapper {

    @Select("SELECT * FROM contact_inquiry WHERE deleted = 0 ORDER BY created_at DESC")
    List<ContactInquiry> findAll();

    @Select("SELECT * FROM contact_inquiry WHERE id = #{id} AND deleted = 0")
    ContactInquiry findById(@Param("id") Long id);

    @Insert("INSERT INTO contact_inquiry (name, email, company, product, message, deleted) " +
            "VALUES (#{name}, #{email}, #{company}, #{product}, #{message}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ContactInquiry inquiry);

    @Update("UPDATE contact_inquiry SET deleted = 1 WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
