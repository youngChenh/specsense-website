package com.specsense.mapper;

import com.specsense.model.entity.CrmOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CrmOrderMapper {

    @Select({"<script>",
            "SELECT o.*, c.name AS customerName, u.username AS ownerName",
            "FROM crm_order o",
            "LEFT JOIN crm_customer c ON o.customer_id = c.id",
            "LEFT JOIN admin_user u ON o.owner_id = u.id",
            "WHERE 1=1",
            "<if test='ownerId != null'> AND o.owner_id = #{ownerId}</if>",
            "<if test='customerId != null'> AND o.customer_id = #{customerId}</if>",
            "<if test='incoterm != null and incoterm != \"\"'> AND o.incoterm = #{incoterm}</if>",
            "<if test='keyword != null and keyword != \"\"'> AND (o.order_no LIKE CONCAT('%', #{keyword}, '%') OR o.supplier_name LIKE CONCAT('%', #{keyword}, '%') OR o.product_name LIKE CONCAT('%', #{keyword}, '%') OR o.model LIKE CONCAT('%', #{keyword}, '%') OR c.name LIKE CONCAT('%', #{keyword}, '%'))</if>",
            "ORDER BY o.id DESC",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"})
    List<CrmOrder> search(@Param("keyword") String keyword,
                          @Param("incoterm") String incoterm,
                          @Param("ownerId") Long ownerId,
                          @Param("customerId") Long customerId,
                          @Param("offset") int offset,
                          @Param("pageSize") int pageSize);

    @Select({"<script>",
            "SELECT COUNT(*) FROM crm_order o LEFT JOIN crm_customer c ON o.customer_id = c.id WHERE 1=1",
            "<if test='ownerId != null'> AND o.owner_id = #{ownerId}</if>",
            "<if test='customerId != null'> AND o.customer_id = #{customerId}</if>",
            "<if test='incoterm != null and incoterm != \"\"'> AND o.incoterm = #{incoterm}</if>",
            "<if test='keyword != null and keyword != \"\"'> AND (o.order_no LIKE CONCAT('%', #{keyword}, '%') OR o.supplier_name LIKE CONCAT('%', #{keyword}, '%') OR o.product_name LIKE CONCAT('%', #{keyword}, '%') OR o.model LIKE CONCAT('%', #{keyword}, '%') OR c.name LIKE CONCAT('%', #{keyword}, '%'))</if>",
            "</script>"})
    long countSearch(@Param("keyword") String keyword,
                     @Param("incoterm") String incoterm,
                     @Param("ownerId") Long ownerId,
                     @Param("customerId") Long customerId);

    @Select("SELECT o.*, c.name AS customerName, u.username AS ownerName FROM crm_order o " +
            "LEFT JOIN crm_customer c ON o.customer_id = c.id LEFT JOIN admin_user u ON o.owner_id = u.id WHERE o.id = #{id}")
    CrmOrder findById(@Param("id") Long id);

    @Select("SELECT o.*, c.name AS customerName, u.username AS ownerName FROM crm_order o " +
            "LEFT JOIN crm_customer c ON o.customer_id = c.id LEFT JOIN admin_user u ON o.owner_id = u.id " +
            "WHERE o.customer_id = #{customerId} ORDER BY o.id DESC")
    List<CrmOrder> findByCustomerId(@Param("customerId") Long customerId);

    @Select({"<script>",
            "SELECT COUNT(*) FROM crm_order WHERE 1=1",
            "<if test='ownerId != null'> AND owner_id = #{ownerId}</if>",
            "</script>"})
    long count(@Param("ownerId") Long ownerId);

    @Insert("INSERT INTO crm_order (customer_id, inquiry_id, owner_id, order_no, order_date, supplier_name, product_name, model, quantity, currency, " +
            "purchase_price, sell_price, incoterm, freight, insurance, bank_fee, tax_included, invoice_type, gross_profit, gross_margin, remark) " +
            "VALUES (#{customerId}, #{inquiryId}, #{ownerId}, #{orderNo}, #{orderDate}, #{supplierName}, #{productName}, #{model}, #{quantity}, #{currency}, " +
            "#{purchasePrice}, #{sellPrice}, #{incoterm}, #{freight}, #{insurance}, #{bankFee}, #{taxIncluded}, #{invoiceType}, #{grossProfit}, #{grossMargin}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CrmOrder order);

    @Update("UPDATE crm_order SET customer_id=#{customerId}, inquiry_id=#{inquiryId}, owner_id=#{ownerId}, order_no=#{orderNo}, order_date=#{orderDate}, " +
            "supplier_name=#{supplierName}, product_name=#{productName}, model=#{model}, quantity=#{quantity}, currency=#{currency}, " +
            "purchase_price=#{purchasePrice}, sell_price=#{sellPrice}, incoterm=#{incoterm}, freight=#{freight}, insurance=#{insurance}, bank_fee=#{bankFee}, " +
            "tax_included=#{taxIncluded}, invoice_type=#{invoiceType}, gross_profit=#{grossProfit}, gross_margin=#{grossMargin}, remark=#{remark} WHERE id=#{id}")
    int update(CrmOrder order);

    @Delete("DELETE FROM crm_order WHERE id=#{id}")
    int delete(@Param("id") Long id);

    @Update("UPDATE crm_order SET customer_id=#{targetCustomerId} WHERE customer_id=#{fromCustomerId}")
    int moveToCustomer(@Param("fromCustomerId") Long fromCustomerId, @Param("targetCustomerId") Long targetCustomerId);
}
