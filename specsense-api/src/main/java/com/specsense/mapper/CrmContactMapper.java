package com.specsense.mapper;

import com.specsense.model.entity.CrmContact;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CrmContactMapper {

    @Select("SELECT t.*, c.name AS customerName FROM crm_contact t LEFT JOIN crm_customer c ON t.customer_id = c.id WHERE t.customer_id = #{customerId} ORDER BY t.id DESC")
    List<CrmContact> findByCustomerId(@Param("customerId") Long customerId);

    @Select({"<script>",
            "SELECT t.*, c.name AS customerName FROM crm_contact t LEFT JOIN crm_customer c ON t.customer_id = c.id",
            "WHERE 1=1",
            "<if test='ownerId != null'> AND c.owner_id = #{ownerId}</if>",
            "ORDER BY t.id DESC",
            "</script>"})
    List<CrmContact> findAll(@Param("ownerId") Long ownerId);

    @Select("SELECT t.*, c.name AS customerName FROM crm_contact t LEFT JOIN crm_customer c ON t.customer_id = c.id WHERE t.id = #{id}")
    CrmContact findById(@Param("id") Long id);

    @Insert("INSERT INTO crm_contact (customer_id, name, title, email, phone, remark) VALUES (#{customerId}, #{name}, #{title}, #{email}, #{phone}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CrmContact contact);

    @Update("UPDATE crm_contact SET name=#{name}, title=#{title}, email=#{email}, phone=#{phone}, remark=#{remark} WHERE id=#{id}")
    int update(CrmContact contact);

    @Update("UPDATE crm_contact SET customer_id=#{targetCustomerId} WHERE customer_id=#{fromCustomerId}")
    int moveToCustomer(@Param("fromCustomerId") Long fromCustomerId, @Param("targetCustomerId") Long targetCustomerId);
}
