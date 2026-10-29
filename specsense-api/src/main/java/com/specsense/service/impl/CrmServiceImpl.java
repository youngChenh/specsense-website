package com.specsense.service.impl;

import com.specsense.crm.CrmAuth;
import com.specsense.mapper.AdminUserMapper;
import com.specsense.mapper.CrmContactMapper;
import com.specsense.mapper.CrmCustomerMapper;
import com.specsense.mapper.CrmAfterSaleMapper;
import com.specsense.mapper.CrmFollowUpMapper;
import com.specsense.mapper.CrmInquiryMapper;
import com.specsense.mapper.CrmOrderMapper;
import com.specsense.model.dto.CrmDuplicateMatch;
import com.specsense.model.dto.CrmImportRow;
import com.specsense.model.entity.ContactInquiry;
import com.specsense.model.entity.CrmContact;
import com.specsense.model.entity.CrmCustomer;
import com.specsense.model.entity.CrmAfterSale;
import com.specsense.model.entity.CrmFollowUp;
import com.specsense.model.entity.CrmInquiry;
import com.specsense.model.entity.CrmOrder;
import com.specsense.model.vo.PageResult;
import com.specsense.service.CrmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CrmServiceImpl implements CrmService {

    private static final List<String> STAGES = Arrays.asList("NEW", "FOLLOWING", "QUOTED", "NEGOTIATING", "WON", "LOST");
    private static final List<String> FUNNEL_STAGES = Arrays.asList("NEW", "FOLLOWING", "QUOTED", "NEGOTIATING");
    private static final List<String> SOURCES = Arrays.asList("website", "exhibition", "outbound", "existing", "social");
    private static final String[] TEMPLATE_HEADERS = {
            "国家", "联系人", "电子邮件", "客户名称", "网站", "行业", "描述", "公司地址", "电话", "手机", "备注"
    };

    @Autowired
    private CrmCustomerMapper customerMapper;
    @Autowired
    private CrmContactMapper contactMapper;
    @Autowired
    private CrmInquiryMapper inquiryMapper;
    @Autowired
    private CrmFollowUpMapper followUpMapper;
    @Autowired
    private CrmOrderMapper orderMapper;
    @Autowired
    private CrmAfterSaleMapper afterSaleMapper;
    @Autowired
    private AdminUserMapper adminUserMapper;

    private void assertCanAccessCustomer(Long customerId, CrmAuth.User user) {
        CrmCustomer customer = customerMapper.findById(customerId);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }
        if (!user.isManager() && (customer.getOwnerId() == null || !customer.getOwnerId().equals(user.getUserId()))) {
            throw new CrmAuth.AuthException(403, "无权访问该客户");
        }
    }

    private void assertCanAccessOwned(Long ownerId, Long customerId, CrmAuth.User user, String missing) {
        if (user.isManager()) {
            return;
        }
        if (ownerId != null && ownerId.equals(user.getUserId())) {
            return;
        }
        if (customerId != null) {
            assertCanAccessCustomer(customerId, user);
            return;
        }
        throw new CrmAuth.AuthException(403, missing);
    }

    private void assertCanAccessOrder(CrmOrder order, CrmAuth.User user) {
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        assertCanAccessOwned(order.getOwnerId(), order.getCustomerId(), user, "无权访问该订单");
    }

    private void assertCanAccessAfterSale(CrmAfterSale item, CrmAuth.User user) {
        if (item == null) {
            throw new RuntimeException("售后记录不存在");
        }
        assertCanAccessOwned(item.getOwnerId(), item.getCustomerId(), user, "无权访问该售后记录");
    }

    private void prepareOrder(CrmOrder order, CrmAuth.User user, CrmOrder existing) {
        if (order.getCustomerId() != null) {
            assertCanAccessCustomer(order.getCustomerId(), user);
        }
        if (user.isSales() || order.getOwnerId() == null) {
            order.setOwnerId(existing != null && existing.getOwnerId() != null && user.isSales()
                    ? existing.getOwnerId() : user.getUserId());
        }
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }
        if (isBlank(order.getCurrency())) {
            order.setCurrency("USD");
        }
        if (order.getQuantity() == null) {
            order.setQuantity(BigDecimal.ONE);
        }
        if (order.getTaxIncluded() == null) {
            order.setTaxIncluded(0);
        }
        fillOrderProfit(order);
    }

    private void prepareAfterSale(CrmAfterSale item, CrmAuth.User user, CrmAfterSale existing) {
        if (item.getOrderId() != null) {
            CrmOrder order = orderMapper.findById(item.getOrderId());
            if (order != null) {
                if (item.getCustomerId() == null) {
                    item.setCustomerId(order.getCustomerId());
                }
                if (isBlank(item.getSupplierName())) {
                    item.setSupplierName(order.getSupplierName());
                }
                if (isBlank(item.getModel())) {
                    item.setModel(order.getModel());
                }
            }
        }
        if (item.getCustomerId() != null) {
            assertCanAccessCustomer(item.getCustomerId(), user);
        }
        if (isBlank(item.getSupplierName()) || isBlank(item.getModel()) || isBlank(item.getProblem())) {
            throw new RuntimeException("供应商、型号和问题描述不能为空");
        }
        if (user.isSales() || item.getOwnerId() == null) {
            item.setOwnerId(existing != null && existing.getOwnerId() != null && user.isSales()
                    ? existing.getOwnerId() : user.getUserId());
        }
        if (item.getResolved() == null) {
            item.setResolved(0);
        }
        if (item.getNeedChangeSupplier() == null) {
            item.setNeedChangeSupplier(0);
        }
        if (item.getResolved() != null && item.getResolved() == 1 && item.getResolvedAt() == null) {
            item.setResolvedAt(LocalDate.now());
        }
    }

    private void fillOrderProfit(CrmOrder order) {
        BigDecimal sell = nz(order.getSellPrice());
        BigDecimal profit = sell.subtract(nz(order.getPurchasePrice()))
                .subtract(nz(order.getFreight()))
                .subtract(nz(order.getInsurance()))
                .subtract(nz(order.getBankFee()));
        order.setGrossProfit(profit);
        if (sell.compareTo(BigDecimal.ZERO) == 0) {
            order.setGrossMargin(BigDecimal.ZERO);
        } else {
            order.setGrossMargin(profit.multiply(new BigDecimal("100")).divide(sell, 2, RoundingMode.HALF_UP));
        }
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private long toLong(Object v) {
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        return 0L;
    }

    private Long resolveOwnerFilter(Long filterOwnerId, CrmAuth.User user) {
        if (user.isManager()) {
            return filterOwnerId;
        }
        return user.getUserId();
    }

    @Override
    public PageResult<List<CrmCustomer>> listCustomers(String keyword, String country, String source, String status,
                                                       Long filterOwnerId, Integer closed, int page, int pageSize, CrmAuth.User user) {
        Long ownerId = resolveOwnerFilter(filterOwnerId, user);
        int offset = (page - 1) * pageSize;
        List<CrmCustomer> list = customerMapper.search(keyword, country, source, status, ownerId, closed, offset, pageSize);
        long total = customerMapper.countSearch(keyword, country, source, status, ownerId, closed);
        return new PageResult<List<CrmCustomer>>(total, page, pageSize, list);
    }

    @Override
    public CrmCustomer getCustomer(Long id, CrmAuth.User user) {
        assertCanAccessCustomer(id, user);
        return customerMapper.findById(id);
    }

    @Override
    public List<CrmDuplicateMatchSafe> checkDuplicates(CrmCustomer customer) {
        return toSafe(customerMapper.findDuplicates(customer.getName(), customer.getWebsite(), null, null, customer.getId()));
    }

    private List<CrmDuplicateMatchSafe> toSafe(List<CrmCustomer> list) {
        List<CrmDuplicateMatchSafe> result = new ArrayList<CrmDuplicateMatchSafe>();
        if (list == null) {
            return result;
        }
        for (CrmCustomer c : list) {
            CrmDuplicateMatchSafe m = new CrmDuplicateMatchSafe();
            m.id = c.getId();
            m.name = c.getName();
            m.country = c.getCountry();
            m.website = c.getWebsite();
            m.reason = buildReason(c, c.getName(), c.getWebsite(), null, null);
            result.add(m);
        }
        return result;
    }

    @Override
    public CrmCustomer createCustomer(CrmCustomer customer, CrmAuth.User user) {
        if (isBlank(customer.getName())) {
            throw new RuntimeException("客户名称不能为空");
        }
        if (customer.getStatus() == null) {
            customer.setStatus("active");
        }
        if (customer.getClosed() == null) {
            customer.setClosed(0);
        }
        if (user.isSales() || customer.getOwnerId() == null) {
            customer.setOwnerId(user.getUserId());
        }
        customerMapper.insert(customer);
        return customerMapper.findById(customer.getId());
    }

    @Override
    public void updateCustomer(CrmCustomer customer, CrmAuth.User user) {
        assertCanAccessCustomer(customer.getId(), user);
        CrmCustomer existing = customerMapper.findById(customer.getId());
        if (!user.isManager()) {
            customer.setOwnerId(existing.getOwnerId());
        }
        customerMapper.update(customer);
    }

    @Override
    public void closeCustomer(Long id, CrmAuth.User user) {
        assertCanAccessCustomer(id, user);
        customerMapper.close(id);
    }

    @Override
    public List<CrmContact> listContacts(Long customerId, CrmAuth.User user) {
        assertCanAccessCustomer(customerId, user);
        return contactMapper.findByCustomerId(customerId);
    }

    @Override
    public CrmContact createContact(CrmContact contact, CrmAuth.User user) {
        if (contact.getCustomerId() == null || isBlank(contact.getName())) {
            throw new RuntimeException("客户和联系人姓名不能为空");
        }
        assertCanAccessCustomer(contact.getCustomerId(), user);
        contactMapper.insert(contact);
        return contactMapper.findById(contact.getId());
    }

    @Override
    public void updateContact(CrmContact contact, CrmAuth.User user) {
        CrmContact existing = contactMapper.findById(contact.getId());
        if (existing == null) {
            throw new RuntimeException("联系人不存在");
        }
        assertCanAccessCustomer(existing.getCustomerId(), user);
        contactMapper.update(contact);
    }

    @Override
    public PageResult<List<CrmInquiry>> listInquiries(String keyword, String country, String source, String stage,
                                                      Long filterOwnerId, Integer unassigned, Long customerId, Integer closed,
                                                      String dateFrom, String dateTo, int page, int pageSize, CrmAuth.User user) {
        Long ownerId = resolveOwnerFilter(filterOwnerId, user);
        Integer unassignedFlag = user.isManager() && unassigned != null && unassigned == 1 ? 1 : null;
        int offset = (page - 1) * pageSize;
        List<CrmInquiry> list = inquiryMapper.search(keyword, country, source, stage, ownerId, unassignedFlag, customerId, closed, dateFrom, dateTo, offset, pageSize);
        long total = inquiryMapper.countSearch(keyword, country, source, stage, ownerId, unassignedFlag, customerId, closed, dateFrom, dateTo);
        return new PageResult<List<CrmInquiry>>(total, page, pageSize, list);
    }

    @Override
    public CrmInquiry getInquiry(Long id, CrmAuth.User user) {
        CrmInquiry inquiry = inquiryMapper.findById(id);
        if (inquiry == null) {
            throw new RuntimeException("询盘不存在");
        }
        assertCanAccessCustomer(inquiry.getCustomerId(), user);
        return inquiry;
    }

    @Override
    public CrmInquiry createInquiry(CrmInquiry inquiry, CrmAuth.User user) {
        if (inquiry.getCustomerId() == null) {
            throw new RuntimeException("请选择客户");
        }
        assertCanAccessCustomer(inquiry.getCustomerId(), user);
        if (inquiry.getInquiryDate() == null) {
            inquiry.setInquiryDate(LocalDate.now());
        }
        if (isBlank(inquiry.getStage())) {
            inquiry.setStage("NEW");
        }
        if (inquiry.getClosed() == null) {
            inquiry.setClosed(0);
        }
        if (user.isSales() || inquiry.getOwnerId() == null) {
            inquiry.setOwnerId(user.getUserId());
        }
        inquiryMapper.insert(inquiry);
        return inquiryMapper.findById(inquiry.getId());
    }

    @Override
    public void updateInquiry(CrmInquiry inquiry, CrmAuth.User user) {
        CrmInquiry existing = getInquiry(inquiry.getId(), user);
        if (!user.isManager()) {
            inquiry.setOwnerId(existing.getOwnerId());
        }
        if (inquiry.getWebsiteInquiryId() == null) {
            inquiry.setWebsiteInquiryId(existing.getWebsiteInquiryId());
        }
        inquiryMapper.update(inquiry);
    }

    @Override
    public void assignInquiryOwner(Long id, Long ownerId, CrmAuth.User user) {
        if (!user.isManager()) {
            throw new CrmAuth.AuthException(403, "只有管理层可以分配销售");
        }
        CrmInquiry inquiry = inquiryMapper.findById(id);
        if (inquiry == null) {
            throw new RuntimeException("询盘不存在");
        }
        inquiryMapper.updateOwner(id, ownerId);
        if (inquiry.getCustomerId() != null) {
            customerMapper.updateOwner(inquiry.getCustomerId(), ownerId);
        }
    }

    @Override
    public CrmInquiry findByWebsiteInquiry(Long websiteInquiryId, String email, CrmAuth.User user) {
        CrmInquiry inquiry = null;
        if (websiteInquiryId != null) {
            inquiry = inquiryMapper.findByWebsiteInquiryId(websiteInquiryId);
        }
        if (inquiry == null && email != null && !email.trim().isEmpty()) {
            inquiry = inquiryMapper.findLatestWebsiteByEmail(email.trim());
        }
        if (inquiry == null) {
            return null;
        }
        if (user.isSales()) {
            assertCanAccessCustomer(inquiry.getCustomerId(), user);
        }
        return inquiry;
    }

    @Override
    public void updateInquiryStage(Long id, String stage, CrmAuth.User user) {
        if (!STAGES.contains(stage)) {
            throw new RuntimeException("无效阶段");
        }
        getInquiry(id, user);
        inquiryMapper.updateStage(id, stage);
    }

    @Override
    public void closeInquiry(Long id, CrmAuth.User user) {
        getInquiry(id, user);
        inquiryMapper.close(id);
    }

    @Override
    public List<CrmFollowUp> listFollowUps(Long customerId, Long inquiryId, CrmAuth.User user) {
        if (inquiryId != null) {
            getInquiry(inquiryId, user);
            return followUpMapper.findByInquiryId(inquiryId);
        }
        if (customerId != null) {
            assertCanAccessCustomer(customerId, user);
            return followUpMapper.findByCustomerId(customerId);
        }
        throw new RuntimeException("缺少客户或询盘");
    }

    @Override
    public CrmFollowUp createFollowUp(CrmFollowUp followUp, CrmAuth.User user) {
        if (followUp.getCustomerId() == null && followUp.getInquiryId() != null) {
            CrmInquiry inquiry = getInquiry(followUp.getInquiryId(), user);
            followUp.setCustomerId(inquiry.getCustomerId());
        }
        if (followUp.getCustomerId() == null) {
            throw new RuntimeException("缺少客户");
        }
        assertCanAccessCustomer(followUp.getCustomerId(), user);
        if (followUp.getFollowDate() == null) {
            followUp.setFollowDate(LocalDate.now());
        }
        followUp.setCreatedBy(user.getUserId());
        followUpMapper.insert(followUp);
        return followUp;
    }

    @Override
    public PageResult<List<CrmOrder>> listOrders(String keyword, String incoterm, Long filterOwnerId, Long customerId,
                                                 int page, int pageSize, CrmAuth.User user) {
        Long ownerId = resolveOwnerFilter(filterOwnerId, user);
        int offset = (page - 1) * pageSize;
        List<CrmOrder> list = orderMapper.search(keyword, incoterm, ownerId, customerId, offset, pageSize);
        long total = orderMapper.countSearch(keyword, incoterm, ownerId, customerId);
        return new PageResult<List<CrmOrder>>(total, page, pageSize, list);
    }

    @Override
    public CrmOrder getOrder(Long id, CrmAuth.User user) {
        CrmOrder order = orderMapper.findById(id);
        assertCanAccessOrder(order, user);
        return order;
    }

    @Override
    public CrmOrder createOrder(CrmOrder order, CrmAuth.User user) {
        prepareOrder(order, user, null);
        orderMapper.insert(order);
        if (isBlank(order.getOrderNo())) {
            order.setOrderNo("O" + order.getId());
            orderMapper.update(order);
        }
        return orderMapper.findById(order.getId());
    }

    @Override
    public void updateOrder(CrmOrder order, CrmAuth.User user) {
        CrmOrder existing = getOrder(order.getId(), user);
        prepareOrder(order, user, existing);
        orderMapper.update(order);
    }

    @Override
    public void deleteOrder(Long id, CrmAuth.User user) {
        getOrder(id, user);
        orderMapper.delete(id);
    }

    @Override
    public PageResult<List<CrmAfterSale>> listAfterSales(String keyword, Integer resolved, Long filterOwnerId, Long customerId,
                                                         int page, int pageSize, CrmAuth.User user) {
        Long ownerId = resolveOwnerFilter(filterOwnerId, user);
        int offset = (page - 1) * pageSize;
        List<CrmAfterSale> list = afterSaleMapper.search(keyword, resolved, ownerId, customerId, offset, pageSize);
        long total = afterSaleMapper.countSearch(keyword, resolved, ownerId, customerId);
        return new PageResult<List<CrmAfterSale>>(total, page, pageSize, list);
    }

    @Override
    public CrmAfterSale getAfterSale(Long id, CrmAuth.User user) {
        CrmAfterSale item = afterSaleMapper.findById(id);
        assertCanAccessAfterSale(item, user);
        return item;
    }

    @Override
    public CrmAfterSale createAfterSale(CrmAfterSale item, CrmAuth.User user) {
        prepareAfterSale(item, user, null);
        afterSaleMapper.insert(item);
        return afterSaleMapper.findById(item.getId());
    }

    @Override
    public void updateAfterSale(CrmAfterSale item, CrmAuth.User user) {
        CrmAfterSale existing = getAfterSale(item.getId(), user);
        prepareAfterSale(item, user, existing);
        afterSaleMapper.update(item);
    }

    @Override
    public void deleteAfterSale(Long id, CrmAuth.User user) {
        getAfterSale(id, user);
        afterSaleMapper.delete(id);
    }

    @Override
    public List<Map<String, Object>> afterSaleStats(CrmAuth.User user) {
        List<Map<String, Object>> rows = afterSaleMapper.statsBySupplierModel(user.scopedOwnerId());
        if (rows == null) {
            return new ArrayList<Map<String, Object>>();
        }
        for (Map<String, Object> row : rows) {
            long open = toLong(row.get("openCount"));
            long issues = toLong(row.get("issueCount"));
            long flags = toLong(row.get("changeFlagCount"));
            row.put("suggestChange", flags > 0 || (issues >= 3 && open >= 2));
        }
        return rows;
    }

    @Override
    public Map<String, Object> dashboard(CrmAuth.User user) {
        Long ownerId = user.scopedOwnerId();
        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> stageRows = inquiryMapper.countByStage(ownerId);
        Map<String, Long> stageCount = new LinkedHashMap<String, Long>();
        for (String s : STAGES) {
            stageCount.put(s, 0L);
        }
        if (stageRows != null) {
            for (Map<String, Object> row : stageRows) {
                Object stage = row.get("stage");
                Object cnt = row.get("cnt");
                if (stage != null && cnt instanceof Number) {
                    stageCount.put(stage.toString(), ((Number) cnt).longValue());
                }
            }
        }
        List<Map<String, Object>> funnel = new ArrayList<Map<String, Object>>();
        for (String s : FUNNEL_STAGES) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("stage", s);
            item.put("count", stageCount.get(s));
            funnel.add(item);
        }
        YearMonth month = YearMonth.now();
        long monthNew = inquiryMapper.countByDateRange(month.atDay(1).toString(), month.atEndOfMonth().toString(), ownerId);
        data.put("funnel", funnel);
        data.put("stageDistribution", stageCount);
        data.put("monthNewInquiries", monthNew);
        data.put("customerCount", customerMapper.countSearch(null, null, null, null, ownerId, 0));
        data.put("inquiryCount", inquiryMapper.count(ownerId, 0));
        data.put("inquiriesByStage", groupOpenInquiries(ownerId));
        data.put("manager", user.isManager());
        data.put("unassignedCount", user.isManager() ? inquiryMapper.countUnassigned() : 0L);
        data.put("pendingFollowCount", inquiryMapper.countPendingFollow(ownerId));
        data.put("unassignedInquiries", user.isManager() ? inquiryMapper.findUnassigned(8) : java.util.Collections.emptyList());
        data.put("orderCount", orderMapper.count(ownerId));
        data.put("openAfterSaleCount", afterSaleMapper.countOpen(ownerId));
        return data;
    }

    private Map<String, List<CrmInquiry>> groupOpenInquiries(Long ownerId) {
        List<CrmInquiry> list = inquiryMapper.search(null, null, null, null, ownerId, null, null, 0, null, null, 0, 200);
        Map<String, List<CrmInquiry>> grouped = new LinkedHashMap<String, List<CrmInquiry>>();
        for (String s : STAGES) {
            grouped.put(s, new ArrayList<CrmInquiry>());
        }
        for (CrmInquiry item : list) {
            if (item.getStage() != null && grouped.containsKey(item.getStage())) {
                grouped.get(item.getStage()).add(item);
            }
        }
        return grouped;
    }

    @Override
    public List<String> countries() {
        return customerMapper.findCountries();
    }

    @Override
    public List<CrmImportRow> previewImport(MultipartFile file) {
        List<CrmImportRow> rows = parseExcel(file);
        for (CrmImportRow row : rows) {
            if (isBlank(row.getName())) {
                row.getErrors().add("客户名称为空");
            }
            if (!isBlank(row.getSource()) && !SOURCES.contains(normalizeSource(row.getSource()))) {
                row.getErrors().add("线索来源不在可选范围内");
            } else if (!isBlank(row.getSource())) {
                row.setSource(normalizeSource(row.getSource()));
            }
            List<String> emails = row.getContactEmails() != null ? row.getContactEmails() : splitEmails(row.getContactEmail());
            row.setContactEmails(emails);
            for (String email : emails) {
                if (!email.contains("@")) {
                    row.getErrors().add("邮箱格式不正确：" + email);
                }
            }
            java.util.LinkedHashMap<Long, CrmCustomer> dupMap = new java.util.LinkedHashMap<Long, CrmCustomer>();
            if (emails.isEmpty()) {
                List<CrmCustomer> dups = customerMapper.findDuplicates(row.getName(), row.getWebsite(), null, row.getContactPhone(), null);
                if (dups != null) {
                    for (CrmCustomer d : dups) {
                        dupMap.put(d.getId(), d);
                    }
                }
            } else {
                for (String email : emails) {
                    List<CrmCustomer> dups = customerMapper.findDuplicates(row.getName(), row.getWebsite(), email, row.getContactPhone(), null);
                    if (dups == null) {
                        continue;
                    }
                    for (CrmCustomer d : dups) {
                        dupMap.put(d.getId(), d);
                    }
                }
            }
            for (CrmCustomer d : dupMap.values()) {
                CrmDuplicateMatch match = new CrmDuplicateMatch();
                match.setId(d.getId());
                match.setName(d.getName());
                match.setCountry(d.getCountry());
                match.setWebsite(d.getWebsite());
                String firstEmail = emails.isEmpty() ? row.getContactEmail() : emails.get(0);
                match.setReason(buildReason(d, row.getName(), row.getWebsite(), firstEmail, row.getContactPhone()));
                row.getDuplicates().add(match);
            }
            if (!row.getDuplicates().isEmpty()) {
                row.setAction("skip");
                row.setMergeTargetId(row.getDuplicates().get(0).getId());
            }
        }
        return rows;
    }

    @Override
    @Transactional
    public Map<String, Object> commitImport(List<CrmImportRow> rows, CrmAuth.User user) {
        if (!user.isManager()) {
            throw new CrmAuth.AuthException(403, "仅管理层可导入");
        }
        int created = 0;
        int merged = 0;
        int skipped = 0;
        Map<String, Long> nameToId = new HashMap<String, Long>();
        for (CrmImportRow row : rows) {
            if (row.getErrors() != null && !row.getErrors().isEmpty()) {
                skipped++;
                continue;
            }
            String action = row.getAction() == null ? "create" : row.getAction();
            if ("skip".equals(action)) {
                skipped++;
                continue;
            }
            Long customerId;
            if ("merge".equals(action) && row.getMergeTargetId() != null) {
                customerId = row.getMergeTargetId();
                mergeCustomerFields(customerId, row);
                merged++;
            } else {
                String key = norm(row.getName());
                if (nameToId.containsKey(key)) {
                    customerId = nameToId.get(key);
                } else {
                    CrmCustomer customer = new CrmCustomer();
                    customer.setName(row.getName());
                    customer.setCountry(row.getCountry());
                    customer.setWebsite(row.getWebsite());
                    customer.setIndustry(row.getIndustry());
                    customer.setAddress(row.getAddress());
                    customer.setSource(row.getSource());
                    customer.setRemark(row.getRemark());
                    customer.setOwnerId(user.getUserId());
                    customer.setStatus("active");
                    customer.setClosed(0);
                    customerMapper.insert(customer);
                    customerId = customer.getId();
                    nameToId.put(key, customerId);
                    created++;
                }
            }
            importContacts(customerId, row);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("created", created);
        result.put("merged", merged);
        result.put("skipped", skipped);
        return result;
    }

    private void mergeCustomerFields(Long customerId, CrmImportRow row) {
        CrmCustomer existing = customerMapper.findById(customerId);
        if (existing == null) {
            return;
        }
        if (isBlank(existing.getCountry())) existing.setCountry(row.getCountry());
        if (isBlank(existing.getWebsite())) existing.setWebsite(row.getWebsite());
        if (isBlank(existing.getIndustry())) existing.setIndustry(row.getIndustry());
        if (isBlank(existing.getAddress())) existing.setAddress(row.getAddress());
        if (isBlank(existing.getSource())) existing.setSource(row.getSource());
        if (isBlank(existing.getRemark())) existing.setRemark(row.getRemark());
        customerMapper.update(existing);
    }

    @Override
    @Transactional
    public void mergeCustomers(Long fromId, Long toId, CrmAuth.User user) {
        if (!user.isManager()) {
            throw new CrmAuth.AuthException(403, "仅管理层可合并");
        }
        CrmCustomer from = customerMapper.findById(fromId);
        CrmCustomer to = customerMapper.findById(toId);
        if (from == null || to == null) {
            throw new RuntimeException("客户不存在");
        }
        contactMapper.moveToCustomer(fromId, toId);
        inquiryMapper.moveToCustomer(fromId, toId);
        followUpMapper.moveToCustomer(fromId, toId);
        orderMapper.moveToCustomer(fromId, toId);
        afterSaleMapper.moveToCustomer(fromId, toId);
        String mergedAttach = joinJson(to.getAttachments(), from.getAttachments());
        to.setAttachments(mergedAttach);
        if (isBlank(to.getCountry())) to.setCountry(from.getCountry());
        if (isBlank(to.getWebsite())) to.setWebsite(from.getWebsite());
        if (isBlank(to.getIndustry())) to.setIndustry(from.getIndustry());
        if (isBlank(to.getAddress())) to.setAddress(from.getAddress());
        customerMapper.update(to);
        customerMapper.close(fromId);
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) {
        try {
            List<String[]> rows = new ArrayList<String[]>();
            rows.add(TEMPLATE_HEADERS);
            rows.add(new String[]{
                    "Germany", "John Smith", "john@example.com、sales@example.com", "ABC Instruments",
                    "https://example.com", "实验室", "光电仪器代理", "Berlin", "+49-111", "+49-222", "展会认识"
            });
            writeSimpleXlsx(response, "crm-import-template.xlsx", "customers", rows);
        } catch (Exception e) {
            throw new RuntimeException("无法生成模板: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void ingestWebsiteInquiry(ContactInquiry inquiry) {
        if (inquiry == null) {
            return;
        }
        String company = !isBlank(inquiry.getCompany()) ? inquiry.getCompany().trim() : inquiry.getName();
        if (isBlank(company)) {
            company = "Website Inquiry";
        }
        List<CrmCustomer> matches = customerMapper.findDuplicates(company, null, inquiry.getEmail(), null, null);
        CrmCustomer customer;
        if (matches != null && !matches.isEmpty()) {
            customer = matches.get(0);
        } else {
            customer = new CrmCustomer();
            customer.setName(company);
            customer.setSource("website");
            customer.setStatus("potential");
            customer.setClosed(0);
            customer.setRemark(inquiry.getProduct());
            customerMapper.insert(customer);
            customer = customerMapper.findById(customer.getId());
        }

        CrmContact contact = null;
        List<CrmContact> contacts = contactMapper.findByCustomerId(customer.getId());
        if (contacts != null) {
            for (CrmContact existing : contacts) {
                if (!isBlank(inquiry.getEmail()) && inquiry.getEmail().equalsIgnoreCase(n(existing.getEmail()))) {
                    contact = existing;
                    break;
                }
                if (contact == null && !isBlank(inquiry.getName()) && inquiry.getName().equalsIgnoreCase(n(existing.getName()))) {
                    contact = existing;
                }
            }
        }
        if (contact == null) {
            contact = new CrmContact();
            contact.setCustomerId(customer.getId());
            contact.setName(isBlank(inquiry.getName()) ? company : inquiry.getName().trim());
            contact.setEmail(inquiry.getEmail());
            contactMapper.insert(contact);
        }

        StringBuilder requirement = new StringBuilder();
        if (!isBlank(inquiry.getProduct())) {
            requirement.append("Product: ").append(inquiry.getProduct().trim());
        }
        if (!isBlank(inquiry.getMessage())) {
            if (requirement.length() > 0) {
                requirement.append("\n");
            }
            requirement.append(inquiry.getMessage().trim());
        }

        CrmInquiry crmInquiry = new CrmInquiry();
        crmInquiry.setCustomerId(customer.getId());
        crmInquiry.setContactId(contact.getId());
        crmInquiry.setInquiryDate(LocalDate.now());
        crmInquiry.setSource("website");
        crmInquiry.setRequirement(requirement.toString());
        crmInquiry.setStage("NEW");
        crmInquiry.setClosed(0);
        crmInquiry.setWebsiteInquiryId(inquiry.getId());
        inquiryMapper.insert(crmInquiry);
    }

    @Override
    public void exportExcel(String type, CrmAuth.User user, HttpServletResponse response) {
        Long ownerId = user.scopedOwnerId();
        try {
            if ("contacts".equals(type)) {
                List<String[]> rows = new ArrayList<String[]>();
                rows.add(new String[]{"客户名称", "联系人", "职位", "邮箱", "电话", "备注"});
                List<CrmContact> list = contactMapper.findAll(ownerId);
                for (CrmContact c : list) {
                    rows.add(new String[]{n(c.getCustomerName()), n(c.getName()), n(c.getTitle()), n(c.getEmail()), n(c.getPhone()), n(c.getRemark())});
                }
                writeSimpleXlsx(response, "crm-contacts.xlsx", "contacts", rows);
            } else if ("inquiries".equals(type)) {
                List<String[]> rows = new ArrayList<String[]>();
                rows.add(new String[]{"客户", "联系人", "询盘日期", "来源", "阶段", "所属销售", "需求", "备注", "是否关闭"});
                List<CrmInquiry> list = inquiryMapper.findAll(ownerId);
                for (CrmInquiry i : list) {
                    rows.add(new String[]{
                            n(i.getCustomerName()), n(i.getContactName()),
                            i.getInquiryDate() == null ? "" : i.getInquiryDate().toString(),
                            sourceLabel(i.getSource()), stageLabel(i.getStage()), n(i.getOwnerName()),
                            n(i.getRequirement()), n(i.getRemark()),
                            i.getClosed() != null && i.getClosed() == 1 ? "是" : "否"
                    });
                }
                writeSimpleXlsx(response, "crm-inquiries.xlsx", "inquiries", rows);
            } else if ("followups".equals(type)) {
                List<String[]> rows = new ArrayList<String[]>();
                rows.add(new String[]{"客户ID", "询盘ID", "跟进日期", "方式", "沟通内容", "结果", "记录人"});
                List<CrmFollowUp> list = followUpMapper.findAll(ownerId);
                for (CrmFollowUp f : list) {
                    rows.add(new String[]{
                            f.getCustomerId() == null ? "" : String.valueOf(f.getCustomerId()),
                            f.getInquiryId() == null ? "" : String.valueOf(f.getInquiryId()),
                            f.getFollowDate() == null ? "" : f.getFollowDate().toString(),
                            methodLabel(f.getMethod()), n(f.getContent()), n(f.getResult()), n(f.getCreatedByName())
                    });
                }
                writeSimpleXlsx(response, "crm-followups.xlsx", "followups", rows);
            } else {
                List<String[]> rows = new ArrayList<String[]>();
                rows.add(new String[]{"客户名称", "国家/地区", "官网", "行业", "地址", "线索来源", "所属销售", "状态", "备注", "是否关闭"});
                List<CrmCustomer> list = customerMapper.findAll(ownerId);
                for (CrmCustomer c : list) {
                    rows.add(new String[]{
                            n(c.getName()), n(c.getCountry()), n(c.getWebsite()), n(c.getIndustry()), n(c.getAddress()),
                            sourceLabel(c.getSource()), n(c.getOwnerName()), n(c.getStatus()), n(c.getRemark()),
                            c.getClosed() != null && c.getClosed() == 1 ? "是" : "否"
                    });
                }
                writeSimpleXlsx(response, "crm-customers.xlsx", "customers", rows);
            }
        } catch (IOException e) {
            throw new RuntimeException("导出失败: " + e.getMessage(), e);
        }
    }

    private void importContacts(Long customerId, CrmImportRow row) {
        List<String> emails = row.getContactEmails();
        if (emails == null || emails.isEmpty()) {
            emails = splitEmails(row.getContactEmail());
        }
        if (isBlank(row.getContactName()) && isBlank(row.getContactPhone()) && emails.isEmpty()) {
            return;
        }
        java.util.Set<String> have = new java.util.HashSet<String>();
        List<CrmContact> existing = contactMapper.findByCustomerId(customerId);
        if (existing != null) {
            for (CrmContact c : existing) {
                if (!isBlank(c.getEmail())) {
                    have.add(norm(c.getEmail()));
                }
            }
        }
        String name = isBlank(row.getContactName()) ? "未命名" : row.getContactName();
        if (emails.isEmpty()) {
            emails = java.util.Collections.singletonList("");
        }
        for (String email : emails) {
            if (!isBlank(email) && have.contains(norm(email))) {
                continue;
            }
            CrmContact contact = new CrmContact();
            contact.setCustomerId(customerId);
            contact.setName(name);
            contact.setTitle(row.getContactTitle());
            contact.setEmail(isBlank(email) ? null : email);
            contact.setPhone(row.getContactPhone());
            contactMapper.insert(contact);
            if (!isBlank(email)) {
                have.add(norm(email));
            }
        }
    }

    private List<String> splitEmails(String raw) {
        List<String> list = new ArrayList<String>();
        if (isBlank(raw)) {
            return list;
        }
        String[] parts = raw.split("[、，,;；\\n]+");
        java.util.LinkedHashSet<String> seen = new java.util.LinkedHashSet<String>();
        for (String part : parts) {
            if (part == null) {
                continue;
            }
            String email = part.trim();
            if (email.isEmpty()) {
                continue;
            }
            String key = email.toLowerCase();
            if (seen.add(key)) {
                list.add(email);
            }
        }
        return list;
    }

    private List<CrmImportRow> parseExcel(MultipartFile file) {
        List<CrmImportRow> rows = new ArrayList<CrmImportRow>();
        try {
            List<List<String>> table = readXlsxTable(file.getBytes());
            if (table.isEmpty()) {
                return rows;
            }
            Map<String, Integer> headers = headerIndex(table.get(0));
            for (int i = 1; i < table.size(); i++) {
                List<String> excelRow = table.get(i);
                CrmImportRow row = new CrmImportRow();
                row.setRowNum(i + 1);
                row.setCountry(col(excelRow, headers, "country", 0));
                row.setContactName(col(excelRow, headers, "contactName", 1));
                row.setContactTitle(col(excelRow, headers, "contactTitle", -1));
                String emailsRaw = col(excelRow, headers, "contactEmail", 2);
                row.setContactEmail(emailsRaw);
                row.setContactEmails(splitEmails(emailsRaw));
                row.setName(col(excelRow, headers, "name", 3));
                row.setWebsite(col(excelRow, headers, "website", 4));
                row.setIndustry(col(excelRow, headers, "industry", 5));
                String description = col(excelRow, headers, "description", 6);
                row.setAddress(col(excelRow, headers, "address", 7));
                String landline = col(excelRow, headers, "landline", 8);
                String mobile = col(excelRow, headers, "mobile", 9);
                String remark = col(excelRow, headers, "remark", 10);
                row.setSource(col(excelRow, headers, "source", -1));
                row.setContactPhone(joinPhone(mobile, landline));
                row.setRemark(joinRemark(description, remark));
                if (isBlank(row.getName()) && isBlank(row.getContactName()) && isBlank(row.getContactEmail())) {
                    continue;
                }
                rows.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("无法解析 Excel，请使用当前模板（.xlsx）。" + (e.getMessage() == null ? "" : e.getMessage()), e);
        }
        return rows;
    }

    private Map<String, Integer> headerIndex(List<String> header) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        if (header == null) {
            return map;
        }
        for (int i = 0; i < header.size(); i++) {
            String key = normalizeHeader(header.get(i));
            if (!isBlank(key) && !map.containsKey(key)) {
                map.put(key, i);
            }
        }
        return map;
    }

    private String normalizeHeader(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.replace("\uFEFF", "").replace("\n", "").replace(" ", "").trim();
        if ("国家".equals(s) || "国家/地区".equals(s)) return "country";
        if ("联系人".equals(s) || "联系人姓名".equals(s)) return "contactName";
        if ("职位".equals(s)) return "contactTitle";
        if ("电子邮件".equals(s) || "邮箱".equals(s) || "邮件".equals(s)) return "contactEmail";
        if ("客户名称".equals(s) || "客户".equals(s)) return "name";
        if ("网站".equals(s) || "官网".equals(s)) return "website";
        if ("行业".equals(s)) return "industry";
        if ("描述".equals(s)) return "description";
        if ("公司地址".equals(s) || "地址".equals(s)) return "address";
        if ("电话".equals(s) || "座机".equals(s)) return "landline";
        if ("手机".equals(s) || "手机号".equals(s)) return "mobile";
        if ("备注".equals(s)) return "remark";
        if ("线索来源".equals(s) || "来源".equals(s)) return "source";
        return s;
    }

    private String col(List<String> row, Map<String, Integer> headers, String key, int fallback) {
        Integer idx = headers.get(key);
        if (idx == null) {
            idx = fallback;
        }
        if (idx == null || idx < 0 || row == null || idx >= row.size()) {
            return "";
        }
        String v = row.get(idx);
        return v == null ? "" : v.trim();
    }

    private List<List<String>> readXlsxTable(byte[] zipBytes) throws IOException {
        Map<String, String> parts = unzipText(zipBytes);
        String sheetXml = null;
        for (Map.Entry<String, String> e : parts.entrySet()) {
            String name = e.getKey().replace('\\', '/');
            if (name.startsWith("xl/worksheets/") && name.endsWith(".xml")) {
                sheetXml = e.getValue();
                if (name.endsWith("sheet1.xml")) {
                    break;
                }
            }
        }
        if (sheetXml == null) {
            throw new IOException("xlsx 中没有工作表");
        }
        List<String> shared = parseSharedStrings(parts.get("xl/sharedStrings.xml"));
        return parseSheetRows(sheetXml, shared);
    }

    private Map<String, String> unzipText(byte[] zipBytes) throws IOException {
        Map<String, String> parts = new HashMap<String, String>();
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = zis.read(buf)) >= 0) {
                bos.write(buf, 0, n);
            }
            parts.put(entry.getName(), bos.toString("UTF-8"));
        }
        zis.close();
        return parts;
    }

    private List<String> parseSharedStrings(String xml) {
        List<String> list = new ArrayList<String>();
        if (xml == null) {
            return list;
        }
        Matcher si = Pattern.compile("(?s)<si>(.*?)</si>").matcher(xml);
        while (si.find()) {
            StringBuilder sb = new StringBuilder();
            Matcher t = Pattern.compile("(?s)<t[^>]*>(.*?)</t>").matcher(si.group(1));
            while (t.find()) {
                sb.append(xmlUnescape(t.group(1)));
            }
            list.add(sb.toString());
        }
        return list;
    }

    private List<List<String>> parseSheetRows(String xml, List<String> shared) {
        List<List<String>> table = new ArrayList<List<String>>();
        Matcher rowM = Pattern.compile("(?s)<row[^>]*>(.*?)</row>").matcher(xml);
        while (rowM.find()) {
            List<String> cells = new ArrayList<String>();
            Matcher cellM = Pattern.compile("(?s)<c([^>]*)>(.*?)</c>|<c([^/>]*)/>").matcher(rowM.group(1));
            while (cellM.find()) {
                String attrs = cellM.group(1) != null ? cellM.group(1) : cellM.group(3);
                String inner = cellM.group(2) == null ? "" : cellM.group(2);
                String ref = attr(attrs, "r");
                int idx = colIndex(ref);
                while (cells.size() <= idx) {
                    cells.add("");
                }
                cells.set(idx, cellText(attr(attrs, "t"), inner, shared));
            }
            boolean empty = true;
            for (String c : cells) {
                if (c != null && !c.trim().isEmpty()) {
                    empty = false;
                    break;
                }
            }
            if (!empty) {
                table.add(cells);
            }
        }
        return table;
    }

    private String cellText(String type, String inner, List<String> shared) {
        if ("inlineStr".equals(type)) {
            Matcher t = Pattern.compile("(?s)<t[^>]*>(.*?)</t>").matcher(inner);
            StringBuilder sb = new StringBuilder();
            while (t.find()) {
                sb.append(xmlUnescape(t.group(1)));
            }
            return sb.toString();
        }
        Matcher v = Pattern.compile("(?s)<v[^>]*>(.*?)</v>").matcher(inner);
        String raw = v.find() ? xmlUnescape(v.group(1).trim()) : "";
        if ("s".equals(type)) {
            try {
                int i = Integer.parseInt(raw);
                if (i >= 0 && i < shared.size()) {
                    return shared.get(i);
                }
            } catch (NumberFormatException ignored) {
            }
            return raw;
        }
        return raw;
    }

    private String attr(String attrs, String name) {
        if (attrs == null) {
            return "";
        }
        Matcher m = Pattern.compile(name + "=\"([^\"]*)\"").matcher(attrs);
        return m.find() ? m.group(1) : "";
    }

    private int colIndex(String ref) {
        if (ref == null) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < ref.length(); i++) {
            char ch = ref.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                n = n * 26 + (ch - 'A' + 1);
            } else if (ch >= 'a' && ch <= 'z') {
                n = n * 26 + (ch - 'a' + 1);
            } else {
                break;
            }
        }
        return Math.max(0, n - 1);
    }

    private String xmlUnescape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace("&amp;", "&");
    }

    private String joinPhone(String mobile, String landline) {
        if (!isBlank(mobile) && !isBlank(landline) && !norm(mobile).equals(norm(landline))) {
            return mobile + " / " + landline;
        }
        if (!isBlank(mobile)) {
            return mobile;
        }
        return n(landline);
    }

    private String joinRemark(String description, String remark) {
        if (!isBlank(description) && !isBlank(remark)) {
            return description + "\n" + remark;
        }
        if (!isBlank(description)) {
            return description;
        }
        return n(remark);
    }

    private void writeSimpleXlsx(HttpServletResponse response, String filename, String sheetName, List<String[]> rows) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);
        putZip(zos, "[Content_Types].xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                        + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                        + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                        + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                        + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                        + "</Types>");
        putZip(zos, "_rels/.rels",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                        + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                        + "</Relationships>");
        putZip(zos, "xl/workbook.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
                        + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                        + "<sheets><sheet name=\"" + xmlEscape(sheetName) + "\" sheetId=\"1\" r:id=\"rId1\"/></sheets>"
                        + "</workbook>");
        putZip(zos, "xl/_rels/workbook.xml.rels",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                        + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                        + "</Relationships>");
        putZip(zos, "xl/worksheets/sheet1.xml", buildSheetXml(rows));
        zos.close();

        byte[] bytes = bos.toByteArray();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setHeader("Cache-Control", "no-store");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.flushBuffer();
    }

    private void putZip(ZipOutputStream zos, String name, String xml) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(xml.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private String buildSheetXml(List<String[]> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
        for (int r = 0; r < rows.size(); r++) {
            String[] cols = rows.get(r);
            sb.append("<row r=\"").append(r + 1).append("\">");
            for (int c = 0; c < cols.length; c++) {
                String ref = String.valueOf((char) ('A' + c)) + (r + 1);
                sb.append("<c r=\"").append(ref).append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">")
                        .append(xmlEscape(cols[c] == null ? "" : cols[c]))
                        .append("</t></is></c>");
            }
            sb.append("</row>");
        }
        sb.append("</sheetData></worksheet>");
        return sb.toString();
    }

    private String xmlEscape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private String buildReason(CrmCustomer existing, String name, String website, String email, String phone) {
        List<String> reasons = new ArrayList<String>();
        if (!isBlank(name) && norm(existing.getName()).equals(norm(name))) {
            reasons.add("客户名称相同");
        }
        if (!isBlank(website) && !isBlank(existing.getWebsite()) && norm(existing.getWebsite()).equals(norm(website))) {
            reasons.add("官网相同");
        }
        if (!isBlank(existing.getCountry()) && reasons.contains("客户名称相同")) {
            reasons.add("国家/地区可参考：" + existing.getCountry());
        }
        if (!isBlank(email)) {
            reasons.add("邮箱可能重复");
        }
        if (!isBlank(phone)) {
            reasons.add("电话可能重复");
        }
        if (reasons.isEmpty()) {
            return "疑似重复";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reasons.size(); i++) {
            if (i > 0) sb.append("；");
            sb.append(reasons.get(i));
        }
        return sb.toString();
    }

    private String normalizeSource(String raw) {
        if (raw == null) return null;
        String v = raw.trim();
        if (SOURCES.contains(v)) return v;
        if ("官网".equals(v) || "website".equalsIgnoreCase(v)) return "website";
        if ("展会".equals(v) || "exhibition".equalsIgnoreCase(v)) return "exhibition";
        if ("主动开发".equals(v) || "outbound".equalsIgnoreCase(v)) return "outbound";
        if ("现有客户".equals(v) || "existing".equalsIgnoreCase(v)) return "existing";
        if ("社交媒体宣传".equals(v) || "social".equalsIgnoreCase(v) || "社交媒体".equals(v)) return "social";
        return v;
    }

    private String sourceLabel(String source) {
        if ("website".equals(source)) return "官网";
        if ("exhibition".equals(source)) return "展会";
        if ("outbound".equals(source)) return "主动开发";
        if ("existing".equals(source)) return "现有客户";
        if ("social".equals(source)) return "社交媒体宣传";
        return n(source);
    }

    private String stageLabel(String stage) {
        if ("NEW".equals(stage)) return "新询盘";
        if ("FOLLOWING".equals(stage)) return "跟进中";
        if ("QUOTED".equals(stage)) return "已报价";
        if ("NEGOTIATING".equals(stage)) return "洽谈中";
        if ("WON".equals(stage)) return "赢单";
        if ("LOST".equals(stage)) return "输单";
        return n(stage);
    }

    private String methodLabel(String method) {
        if ("email".equals(method)) return "邮件";
        if ("phone".equals(method)) return "电话";
        if ("whatsapp".equals(method)) return "WhatsApp";
        if ("meeting".equals(method)) return "会议";
        if ("other".equals(method)) return "其他";
        return n(method);
    }

    private String joinJson(String a, String b) {
        if (isBlank(a)) return b;
        if (isBlank(b)) return a;
        String left = a.trim();
        String right = b.trim();
        if (left.startsWith("[") && left.endsWith("]") && right.startsWith("[") && right.endsWith("]")) {
            String innerA = left.substring(1, left.length() - 1).trim();
            String innerB = right.substring(1, right.length() - 1).trim();
            if (isBlank(innerA)) return right;
            if (isBlank(innerB)) return left;
            return "[" + innerA + "," + innerB + "]";
        }
        return left + "\n" + right;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String n(String s) {
        return s == null ? "" : s;
    }

    private String norm(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }
}
