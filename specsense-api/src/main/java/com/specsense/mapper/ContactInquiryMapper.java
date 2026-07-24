package com.specsense.mapper;

import com.specsense.model.entity.ContactInquiry;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ContactInquiryMapper {

    @Select("SELECT * FROM contact_inquiry ORDER BY created_at DESC")
    List<ContactInquiry> findAll();

    @Select("SELECT * FROM contact_inquiry WHERE id = #{id}")
    ContactInquiry findById(@Param("id") Long id);

    @Insert("INSERT INTO contact_inquiry (name, email, company, product, message) " +
            "VALUES (#{name}, #{email}, #{company}, #{product}, #{message})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ContactInquiry inquiry);

    @Delete("DELETE FROM contact_inquiry WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
