package com.specsense.mapper;

import com.specsense.model.entity.CrmFollowUp;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CrmFollowUpMapper {

    @Select("SELECT f.*, u.username AS createdByName FROM crm_follow_up f LEFT JOIN admin_user u ON f.created_by = u.id WHERE f.inquiry_id = #{inquiryId} ORDER BY f.follow_date DESC, f.id DESC")
    List<CrmFollowUp> findByInquiryId(@Param("inquiryId") Long inquiryId);

    @Select("SELECT f.*, u.username AS createdByName FROM crm_follow_up f LEFT JOIN admin_user u ON f.created_by = u.id WHERE f.customer_id = #{customerId} ORDER BY f.follow_date DESC, f.id DESC")
    List<CrmFollowUp> findByCustomerId(@Param("customerId") Long customerId);

    @Select({"<script>",
            "SELECT f.*, u.username AS createdByName FROM crm_follow_up f",
            "LEFT JOIN admin_user u ON f.created_by = u.id",
            "LEFT JOIN crm_customer c ON f.customer_id = c.id",
            "WHERE 1=1",
            "<if test='ownerId != null'> AND c.owner_id = #{ownerId}</if>",
            "ORDER BY f.id DESC",
            "</script>"})
    List<CrmFollowUp> findAll(@Param("ownerId") Long ownerId);

    @Insert("INSERT INTO crm_follow_up (inquiry_id, customer_id, follow_date, method, content, result, attachments, created_by) " +
            "VALUES (#{inquiryId}, #{customerId}, #{followDate}, #{method}, #{content}, #{result}, #{attachments}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CrmFollowUp followUp);

    @Update("UPDATE crm_follow_up SET customer_id=#{targetCustomerId} WHERE customer_id=#{fromCustomerId}")
    int moveToCustomer(@Param("fromCustomerId") Long fromCustomerId, @Param("targetCustomerId") Long targetCustomerId);
}
