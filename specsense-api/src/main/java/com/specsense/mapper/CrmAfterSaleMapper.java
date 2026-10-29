package com.specsense.mapper;

import com.specsense.model.entity.CrmAfterSale;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CrmAfterSaleMapper {

    @Select({"<script>",
            "SELECT a.*, c.name AS customerName, u.username AS ownerName, o.order_no AS orderNo",
            "FROM crm_after_sale a",
            "LEFT JOIN crm_customer c ON a.customer_id = c.id",
            "LEFT JOIN admin_user u ON a.owner_id = u.id",
            "LEFT JOIN crm_order o ON a.order_id = o.id",
            "WHERE 1=1",
            "<if test='ownerId != null'> AND a.owner_id = #{ownerId}</if>",
            "<if test='customerId != null'> AND a.customer_id = #{customerId}</if>",
            "<if test='resolved != null'> AND a.resolved = #{resolved}</if>",
            "<if test='keyword != null and keyword != \"\"'> AND (a.supplier_name LIKE CONCAT('%', #{keyword}, '%') OR a.model LIKE CONCAT('%', #{keyword}, '%') OR a.problem LIKE CONCAT('%', #{keyword}, '%') OR c.name LIKE CONCAT('%', #{keyword}, '%'))</if>",
            "ORDER BY a.id DESC",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"})
    List<CrmAfterSale> search(@Param("keyword") String keyword,
                              @Param("resolved") Integer resolved,
                              @Param("ownerId") Long ownerId,
                              @Param("customerId") Long customerId,
                              @Param("offset") int offset,
                              @Param("pageSize") int pageSize);

    @Select({"<script>",
            "SELECT COUNT(*) FROM crm_after_sale a LEFT JOIN crm_customer c ON a.customer_id = c.id WHERE 1=1",
            "<if test='ownerId != null'> AND a.owner_id = #{ownerId}</if>",
            "<if test='customerId != null'> AND a.customer_id = #{customerId}</if>",
            "<if test='resolved != null'> AND a.resolved = #{resolved}</if>",
            "<if test='keyword != null and keyword != \"\"'> AND (a.supplier_name LIKE CONCAT('%', #{keyword}, '%') OR a.model LIKE CONCAT('%', #{keyword}, '%') OR a.problem LIKE CONCAT('%', #{keyword}, '%') OR c.name LIKE CONCAT('%', #{keyword}, '%'))</if>",
            "</script>"})
    long countSearch(@Param("keyword") String keyword,
                     @Param("resolved") Integer resolved,
                     @Param("ownerId") Long ownerId,
                     @Param("customerId") Long customerId);

    @Select("SELECT a.*, c.name AS customerName, u.username AS ownerName, o.order_no AS orderNo FROM crm_after_sale a " +
            "LEFT JOIN crm_customer c ON a.customer_id = c.id LEFT JOIN admin_user u ON a.owner_id = u.id " +
            "LEFT JOIN crm_order o ON a.order_id = o.id WHERE a.id = #{id}")
    CrmAfterSale findById(@Param("id") Long id);

    @Select("SELECT a.*, c.name AS customerName, u.username AS ownerName FROM crm_after_sale a " +
            "LEFT JOIN crm_customer c ON a.customer_id = c.id LEFT JOIN admin_user u ON a.owner_id = u.id " +
            "WHERE a.customer_id = #{customerId} ORDER BY a.id DESC")
    List<CrmAfterSale> findByCustomerId(@Param("customerId") Long customerId);

    @Select({"<script>",
            "SELECT COUNT(*) FROM crm_after_sale WHERE resolved = 0",
            "<if test='ownerId != null'> AND owner_id = #{ownerId}</if>",
            "</script>"})
    long countOpen(@Param("ownerId") Long ownerId);

    @Select({"<script>",
            "SELECT a.supplier_name AS supplierName, a.model AS model, COUNT(*) AS issueCount,",
            "SUM(CASE WHEN a.resolved = 0 THEN 1 ELSE 0 END) AS openCount,",
            "SUM(CASE WHEN a.need_change_supplier = 1 THEN 1 ELSE 0 END) AS changeFlagCount",
            "FROM crm_after_sale a WHERE 1=1",
            "<if test='ownerId != null'> AND a.owner_id = #{ownerId}</if>",
            "GROUP BY a.supplier_name, a.model",
            "ORDER BY issueCount DESC, openCount DESC",
            "</script>"})
    List<Map<String, Object>> statsBySupplierModel(@Param("ownerId") Long ownerId);

    @Insert("INSERT INTO crm_after_sale (customer_id, order_id, owner_id, supplier_name, model, problem, resolved, resolved_at, need_change_supplier, remark) " +
            "VALUES (#{customerId}, #{orderId}, #{ownerId}, #{supplierName}, #{model}, #{problem}, #{resolved}, #{resolvedAt}, #{needChangeSupplier}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CrmAfterSale item);

    @Update("UPDATE crm_after_sale SET customer_id=#{customerId}, order_id=#{orderId}, owner_id=#{ownerId}, supplier_name=#{supplierName}, model=#{model}, " +
            "problem=#{problem}, resolved=#{resolved}, resolved_at=#{resolvedAt}, need_change_supplier=#{needChangeSupplier}, remark=#{remark} WHERE id=#{id}")
    int update(CrmAfterSale item);

    @Delete("DELETE FROM crm_after_sale WHERE id=#{id}")
    int delete(@Param("id") Long id);

    @Update("UPDATE crm_after_sale SET customer_id=#{targetCustomerId} WHERE customer_id=#{fromCustomerId}")
    int moveToCustomer(@Param("fromCustomerId") Long fromCustomerId, @Param("targetCustomerId") Long targetCustomerId);
}
