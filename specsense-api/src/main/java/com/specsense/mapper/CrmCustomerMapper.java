package com.specsense.mapper;

import com.specsense.model.entity.CrmCustomer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CrmCustomerMapper {

    @Select({"<script>",
            "SELECT c.*, u.username AS ownerName,",
            "(SELECT COUNT(*) FROM crm_contact t WHERE t.customer_id = c.id) AS contactCount,",
            "(SELECT COUNT(*) FROM crm_inquiry i WHERE i.customer_id = c.id) AS inquiryCount",
            "FROM crm_customer c LEFT JOIN admin_user u ON c.owner_id = u.id",
            "WHERE 1=1",
            "<if test='ownerId != null'> AND c.owner_id = #{ownerId}</if>",
            "<if test='keyword != null and keyword != \"\"'> AND (c.name LIKE CONCAT('%', #{keyword}, '%') OR c.country LIKE CONCAT('%', #{keyword}, '%') OR c.website LIKE CONCAT('%', #{keyword}, '%'))</if>",
            "<if test='country != null and country != \"\"'> AND c.country = #{country}</if>",
            "<if test='source != null and source != \"\"'> AND c.source = #{source}</if>",
            "<if test='status != null and status != \"\"'> AND c.status = #{status}</if>",
            "<if test='closed != null'> AND c.closed = #{closed}</if>",
            "ORDER BY c.id DESC",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"})
    List<CrmCustomer> search(@Param("keyword") String keyword,
                             @Param("country") String country,
                             @Param("source") String source,
                             @Param("status") String status,
                             @Param("ownerId") Long ownerId,
                             @Param("closed") Integer closed,
                             @Param("offset") int offset,
                             @Param("pageSize") int pageSize);

    @Select({"<script>",
            "SELECT COUNT(*) FROM crm_customer c WHERE 1=1",
            "<if test='ownerId != null'> AND c.owner_id = #{ownerId}</if>",
            "<if test='keyword != null and keyword != \"\"'> AND (c.name LIKE CONCAT('%', #{keyword}, '%') OR c.country LIKE CONCAT('%', #{keyword}, '%') OR c.website LIKE CONCAT('%', #{keyword}, '%'))</if>",
            "<if test='country != null and country != \"\"'> AND c.country = #{country}</if>",
            "<if test='source != null and source != \"\"'> AND c.source = #{source}</if>",
            "<if test='status != null and status != \"\"'> AND c.status = #{status}</if>",
            "<if test='closed != null'> AND c.closed = #{closed}</if>",
            "</script>"})
    long countSearch(@Param("keyword") String keyword,
                     @Param("country") String country,
                     @Param("source") String source,
                     @Param("status") String status,
                     @Param("ownerId") Long ownerId,
                     @Param("closed") Integer closed);

    @Select({"<script>",
            "SELECT c.*, u.username AS ownerName FROM crm_customer c LEFT JOIN admin_user u ON c.owner_id = u.id WHERE 1=1",
            "<if test='ownerId != null'> AND c.owner_id = #{ownerId}</if>",
            "ORDER BY c.id DESC",
            "</script>"})
    List<CrmCustomer> findAll(@Param("ownerId") Long ownerId);

    @Select("SELECT c.*, u.username AS ownerName FROM crm_customer c LEFT JOIN admin_user u ON c.owner_id = u.id WHERE c.id = #{id}")
    CrmCustomer findById(@Param("id") Long id);

    @Select("SELECT * FROM crm_customer WHERE LOWER(TRIM(name)) = LOWER(TRIM(#{name}))")
    List<CrmCustomer> findByName(@Param("name") String name);

    @Select({"<script>",
            "SELECT DISTINCT c.* FROM crm_customer c",
            "LEFT JOIN crm_contact t ON t.customer_id = c.id",
            "WHERE (#{name} IS NOT NULL AND #{name} != '' AND LOWER(TRIM(c.name)) = LOWER(TRIM(#{name})))",
            "OR (#{website} IS NOT NULL AND #{website} != '' AND LOWER(TRIM(c.website)) = LOWER(TRIM(#{website})))",
            "OR (#{email} IS NOT NULL AND #{email} != '' AND LOWER(TRIM(t.email)) = LOWER(TRIM(#{email})))",
            "OR (#{phone} IS NOT NULL AND #{phone} != '' AND REPLACE(REPLACE(t.phone,' ',''),'-','') = REPLACE(REPLACE(#{phone},' ',''),'-',''))",
            "<if test='excludeId != null'> AND c.id != #{excludeId}</if>",
            "</script>"})
    List<CrmCustomer> findDuplicates(@Param("name") String name,
                                     @Param("website") String website,
                                     @Param("email") String email,
                                     @Param("phone") String phone,
                                     @Param("excludeId") Long excludeId);

    @Insert("INSERT INTO crm_customer (name, country, website, industry, address, source, owner_id, status, remark, attachments, closed) " +
            "VALUES (#{name}, #{country}, #{website}, #{industry}, #{address}, #{source}, #{ownerId}, #{status}, #{remark}, #{attachments}, #{closed})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CrmCustomer customer);

    @Update("UPDATE crm_customer SET name=#{name}, country=#{country}, website=#{website}, industry=#{industry}, address=#{address}, " +
            "source=#{source}, owner_id=#{ownerId}, status=#{status}, remark=#{remark}, attachments=#{attachments}, closed=#{closed} WHERE id=#{id}")
    int update(CrmCustomer customer);

    @Update("UPDATE crm_customer SET owner_id=#{ownerId} WHERE id=#{id}")
    int updateOwner(@Param("id") Long id, @Param("ownerId") Long ownerId);

    @Update("UPDATE crm_customer SET closed=1, status='closed' WHERE id=#{id}")
    int close(@Param("id") Long id);

    @Select("SELECT DISTINCT country FROM crm_customer WHERE country IS NOT NULL AND country != '' ORDER BY country")
    List<String> findCountries();
}
