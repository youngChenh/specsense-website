package com.specsense.mapper;

import com.specsense.model.entity.CrmInquiry;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CrmInquiryMapper {

    @Select({"<script>",
            "SELECT i.*, c.name AS customerName, t.name AS contactName, t.email AS contactEmail, t.phone AS contactPhone, u.username AS ownerName",
            "FROM crm_inquiry i",
            "LEFT JOIN crm_customer c ON i.customer_id = c.id",
            "LEFT JOIN crm_contact t ON i.contact_id = t.id",
            "LEFT JOIN admin_user u ON i.owner_id = u.id",
            "WHERE 1=1",
            "<if test='ownerId != null'> AND i.owner_id = #{ownerId}</if>",
            "<if test='unassigned != null and unassigned == 1'> AND i.owner_id IS NULL</if>",
            "<if test='customerId != null'> AND i.customer_id = #{customerId}</if>",
            "<if test='keyword != null and keyword != \"\"'> AND (c.name LIKE CONCAT('%', #{keyword}, '%') OR i.requirement LIKE CONCAT('%', #{keyword}, '%'))</if>",
            "<if test='country != null and country != \"\"'> AND c.country = #{country}</if>",
            "<if test='source != null and source != \"\"'> AND i.source = #{source}</if>",
            "<if test='stage != null and stage != \"\"'> AND i.stage = #{stage}</if>",
            "<if test='closed != null'> AND i.closed = #{closed}</if>",
            "<if test='dateFrom != null and dateFrom != \"\"'> AND i.inquiry_date &gt;= #{dateFrom}</if>",
            "<if test='dateTo != null and dateTo != \"\"'> AND i.inquiry_date &lt;= #{dateTo}</if>",
            "ORDER BY i.id DESC",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"})
    List<CrmInquiry> search(@Param("keyword") String keyword,
                            @Param("country") String country,
                            @Param("source") String source,
                            @Param("stage") String stage,
                            @Param("ownerId") Long ownerId,
                            @Param("unassigned") Integer unassigned,
                            @Param("customerId") Long customerId,
                            @Param("closed") Integer closed,
                            @Param("dateFrom") String dateFrom,
                            @Param("dateTo") String dateTo,
                            @Param("offset") int offset,
                            @Param("pageSize") int pageSize);

    @Select({"<script>",
            "SELECT COUNT(*) FROM crm_inquiry i LEFT JOIN crm_customer c ON i.customer_id = c.id WHERE 1=1",
            "<if test='ownerId != null'> AND i.owner_id = #{ownerId}</if>",
            "<if test='unassigned != null and unassigned == 1'> AND i.owner_id IS NULL</if>",
            "<if test='customerId != null'> AND i.customer_id = #{customerId}</if>",
            "<if test='keyword != null and keyword != \"\"'> AND (c.name LIKE CONCAT('%', #{keyword}, '%') OR i.requirement LIKE CONCAT('%', #{keyword}, '%'))</if>",
            "<if test='country != null and country != \"\"'> AND c.country = #{country}</if>",
            "<if test='source != null and source != \"\"'> AND i.source = #{source}</if>",
            "<if test='stage != null and stage != \"\"'> AND i.stage = #{stage}</if>",
            "<if test='closed != null'> AND i.closed = #{closed}</if>",
            "<if test='dateFrom != null and dateFrom != \"\"'> AND i.inquiry_date &gt;= #{dateFrom}</if>",
            "<if test='dateTo != null and dateTo != \"\"'> AND i.inquiry_date &lt;= #{dateTo}</if>",
            "</script>"})
    long countSearch(@Param("keyword") String keyword,
                     @Param("country") String country,
                     @Param("source") String source,
                     @Param("stage") String stage,
                     @Param("ownerId") Long ownerId,
                     @Param("unassigned") Integer unassigned,
                     @Param("customerId") Long customerId,
                     @Param("closed") Integer closed,
                     @Param("dateFrom") String dateFrom,
                     @Param("dateTo") String dateTo);

    @Select({"<script>",
            "SELECT i.*, c.name AS customerName, t.name AS contactName, u.username AS ownerName",
            "FROM crm_inquiry i LEFT JOIN crm_customer c ON i.customer_id = c.id",
            "LEFT JOIN crm_contact t ON i.contact_id = t.id",
            "LEFT JOIN admin_user u ON i.owner_id = u.id WHERE 1=1",
            "<if test='ownerId != null'> AND i.owner_id = #{ownerId}</if>",
            "ORDER BY i.id DESC",
            "</script>"})
    List<CrmInquiry> findAll(@Param("ownerId") Long ownerId);

    @Select("SELECT i.*, c.name AS customerName, t.name AS contactName, t.email AS contactEmail, t.phone AS contactPhone, u.username AS ownerName " +
            "FROM crm_inquiry i LEFT JOIN crm_customer c ON i.customer_id = c.id " +
            "LEFT JOIN crm_contact t ON i.contact_id = t.id LEFT JOIN admin_user u ON i.owner_id = u.id WHERE i.id = #{id}")
    CrmInquiry findById(@Param("id") Long id);

    @Select("SELECT i.*, c.name AS customerName, t.name AS contactName, u.username AS ownerName FROM crm_inquiry i " +
            "LEFT JOIN crm_customer c ON i.customer_id = c.id LEFT JOIN crm_contact t ON i.contact_id = t.id " +
            "LEFT JOIN admin_user u ON i.owner_id = u.id WHERE i.customer_id = #{customerId} ORDER BY i.id DESC")
    List<CrmInquiry> findByCustomerId(@Param("customerId") Long customerId);

    @Insert("INSERT INTO crm_inquiry (customer_id, contact_id, inquiry_date, source, requirement, stage, owner_id, next_follow_date, remark, attachments, closed, website_inquiry_id) " +
            "VALUES (#{customerId}, #{contactId}, #{inquiryDate}, #{source}, #{requirement}, #{stage}, #{ownerId}, #{nextFollowDate}, #{remark}, #{attachments}, #{closed}, #{websiteInquiryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CrmInquiry inquiry);

    @Update("UPDATE crm_inquiry SET customer_id=#{customerId}, contact_id=#{contactId}, inquiry_date=#{inquiryDate}, source=#{source}, " +
            "requirement=#{requirement}, stage=#{stage}, owner_id=#{ownerId}, next_follow_date=#{nextFollowDate}, remark=#{remark}, " +
            "attachments=#{attachments}, closed=#{closed}, website_inquiry_id=#{websiteInquiryId} WHERE id=#{id}")
    int update(CrmInquiry inquiry);

    @Update("UPDATE crm_inquiry SET owner_id=#{ownerId} WHERE id=#{id}")
    int updateOwner(@Param("id") Long id, @Param("ownerId") Long ownerId);

    @Select("SELECT i.*, c.name AS customerName, t.name AS contactName, t.email AS contactEmail, t.phone AS contactPhone, u.username AS ownerName " +
            "FROM crm_inquiry i LEFT JOIN crm_customer c ON i.customer_id = c.id " +
            "LEFT JOIN crm_contact t ON i.contact_id = t.id LEFT JOIN admin_user u ON i.owner_id = u.id " +
            "WHERE i.website_inquiry_id = #{websiteInquiryId} ORDER BY i.id DESC LIMIT 1")
    CrmInquiry findByWebsiteInquiryId(@Param("websiteInquiryId") Long websiteInquiryId);

    @Select("SELECT i.*, c.name AS customerName, t.name AS contactName, t.email AS contactEmail, t.phone AS contactPhone, u.username AS ownerName " +
            "FROM crm_inquiry i LEFT JOIN crm_customer c ON i.customer_id = c.id " +
            "LEFT JOIN crm_contact t ON i.contact_id = t.id LEFT JOIN admin_user u ON i.owner_id = u.id " +
            "WHERE i.source = 'website' AND LOWER(TRIM(t.email)) = LOWER(TRIM(#{email})) ORDER BY i.id DESC LIMIT 1")
    CrmInquiry findLatestWebsiteByEmail(@Param("email") String email);

    @Select({"<script>",
            "SELECT COUNT(*) FROM crm_inquiry WHERE closed = 0 AND owner_id IS NULL",
            "</script>"})
    long countUnassigned();

    @Select({"<script>",
            "SELECT COUNT(*) FROM crm_inquiry WHERE closed = 0 AND (",
            "  next_follow_date &lt;= CURDATE() OR (stage = 'NEW' AND (next_follow_date IS NULL))",
            ")",
            "<if test='ownerId != null'> AND owner_id = #{ownerId}</if>",
            "</script>"})
    long countPendingFollow(@Param("ownerId") Long ownerId);

    @Select("SELECT i.*, c.name AS customerName, t.name AS contactName, u.username AS ownerName " +
            "FROM crm_inquiry i LEFT JOIN crm_customer c ON i.customer_id = c.id " +
            "LEFT JOIN crm_contact t ON i.contact_id = t.id LEFT JOIN admin_user u ON i.owner_id = u.id " +
            "WHERE i.closed = 0 AND i.owner_id IS NULL ORDER BY i.id DESC LIMIT #{limit}")
    List<CrmInquiry> findUnassigned(@Param("limit") int limit);

    @Update("UPDATE crm_inquiry SET stage=#{stage} WHERE id=#{id}")
    int updateStage(@Param("id") Long id, @Param("stage") String stage);

    @Update("UPDATE crm_inquiry SET closed=1 WHERE id=#{id}")
    int close(@Param("id") Long id);

    @Update("UPDATE crm_inquiry SET customer_id=#{targetCustomerId} WHERE customer_id=#{fromCustomerId}")
    int moveToCustomer(@Param("fromCustomerId") Long fromCustomerId, @Param("targetCustomerId") Long targetCustomerId);

    @Select({"<script>",
            "SELECT stage, COUNT(*) AS cnt FROM crm_inquiry WHERE closed = 0",
            "<if test='ownerId != null'> AND owner_id = #{ownerId}</if>",
            "GROUP BY stage",
            "</script>"})
    List<Map<String, Object>> countByStage(@Param("ownerId") Long ownerId);

    @Select({"<script>",
            "SELECT COUNT(*) FROM crm_inquiry WHERE closed = 0",
            "AND inquiry_date &gt;= #{from} AND inquiry_date &lt;= #{to}",
            "<if test='ownerId != null'> AND owner_id = #{ownerId}</if>",
            "</script>"})
    long countByDateRange(@Param("from") String from, @Param("to") String to, @Param("ownerId") Long ownerId);

    @Select({"<script>",
            "SELECT COUNT(*) FROM crm_inquiry WHERE 1=1",
            "<if test='ownerId != null'> AND owner_id = #{ownerId}</if>",
            "<if test='closed != null'> AND closed = #{closed}</if>",
            "</script>"})
    long count(@Param("ownerId") Long ownerId, @Param("closed") Integer closed);
}
