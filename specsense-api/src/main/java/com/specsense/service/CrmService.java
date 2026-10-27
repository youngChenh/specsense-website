package com.specsense.service;

import com.specsense.crm.CrmAuth;
import com.specsense.model.dto.CrmImportRow;
import com.specsense.model.entity.CrmContact;
import com.specsense.model.entity.CrmCustomer;
import com.specsense.model.entity.ContactInquiry;
import com.specsense.model.entity.CrmFollowUp;
import com.specsense.model.entity.CrmInquiry;
import com.specsense.model.vo.PageResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface CrmService {
    PageResult<List<CrmCustomer>> listCustomers(String keyword, String country, String source, String status,
                                                Long filterOwnerId, Integer closed, int page, int pageSize, CrmAuth.User user);

    CrmCustomer getCustomer(Long id, CrmAuth.User user);

    List<CrmDuplicateMatchSafe> checkDuplicates(CrmCustomer customer);

    CrmCustomer createCustomer(CrmCustomer customer, CrmAuth.User user);

    void updateCustomer(CrmCustomer customer, CrmAuth.User user);

    void closeCustomer(Long id, CrmAuth.User user);

    List<CrmContact> listContacts(Long customerId, CrmAuth.User user);

    CrmContact createContact(CrmContact contact, CrmAuth.User user);

    void updateContact(CrmContact contact, CrmAuth.User user);

    PageResult<List<CrmInquiry>> listInquiries(String keyword, String country, String source, String stage,
                                               Long filterOwnerId, Integer unassigned, Long customerId, Integer closed,
                                               String dateFrom, String dateTo, int page, int pageSize, CrmAuth.User user);

    CrmInquiry getInquiry(Long id, CrmAuth.User user);

    CrmInquiry createInquiry(CrmInquiry inquiry, CrmAuth.User user);

    void updateInquiry(CrmInquiry inquiry, CrmAuth.User user);

    void updateInquiryStage(Long id, String stage, CrmAuth.User user);

    void assignInquiryOwner(Long id, Long ownerId, CrmAuth.User user);

    CrmInquiry findByWebsiteInquiry(Long websiteInquiryId, String email, CrmAuth.User user);

    void closeInquiry(Long id, CrmAuth.User user);

    List<CrmFollowUp> listFollowUps(Long customerId, Long inquiryId, CrmAuth.User user);

    CrmFollowUp createFollowUp(CrmFollowUp followUp, CrmAuth.User user);

    Map<String, Object> dashboard(CrmAuth.User user);

    List<String> countries();

    List<CrmImportRow> previewImport(MultipartFile file);

    Map<String, Object> commitImport(List<CrmImportRow> rows, CrmAuth.User user);

    void mergeCustomers(Long fromId, Long toId, CrmAuth.User user);

    void downloadTemplate(HttpServletResponse response);

    void exportExcel(String type, CrmAuth.User user, HttpServletResponse response);

    void ingestWebsiteInquiry(ContactInquiry inquiry);

    class CrmDuplicateMatchSafe {
        public Long id;
        public String name;
        public String country;
        public String website;
        public String reason;
    }
}
