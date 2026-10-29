package com.specsense.controller;

import com.specsense.crm.CrmAuth;
import com.specsense.model.dto.CrmImportRow;
import com.specsense.model.entity.CrmContact;
import com.specsense.model.entity.CrmCustomer;
import com.specsense.model.entity.CrmAfterSale;
import com.specsense.model.entity.CrmFollowUp;
import com.specsense.model.entity.CrmInquiry;
import com.specsense.model.entity.CrmOrder;
import com.specsense.model.vo.PageResult;
import com.specsense.model.vo.Result;
import com.specsense.service.CrmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/crm")
public class CrmController {

    @Autowired
    private CrmService crmService;

    @Autowired
    private CrmAuth crmAuth;

    private Result<?> fail(Exception e) {
        if (e instanceof CrmAuth.AuthException) {
            CrmAuth.AuthException ae = (CrmAuth.AuthException) e;
            return Result.error(ae.getCode(), ae.getMessage());
        }
        return Result.error(e.getMessage());
    }

    @GetMapping("/dashboard")
    public Result<?> dashboard(HttpServletRequest request) {
        try {
            return Result.success(crmService.dashboard(crmAuth.requireUser(request)));
        } catch (Exception e) {
            return fail(e);
        }
    }

    @GetMapping("/countries")
    public Result<?> countries(HttpServletRequest request) {
        try {
            crmAuth.requireUser(request);
            return Result.success(crmService.countries());
        } catch (Exception e) {
            return fail(e);
        }
    }

    @GetMapping("/customers")
    public Result<PageResult<List<CrmCustomer>>> customers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Integer closed,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {
        try {
            return Result.success(crmService.listCustomers(keyword, country, source, status, ownerId, closed, page, pageSize, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @GetMapping("/customers/{id}")
    public Result<CrmCustomer> customer(@PathVariable Long id, HttpServletRequest request) {
        try {
            return Result.success(crmService.getCustomer(id, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PostMapping("/customers/duplicates")
    public Result<?> duplicates(@RequestBody CrmCustomer customer, HttpServletRequest request) {
        try {
            crmAuth.requireUser(request);
            return Result.success(crmService.checkDuplicates(customer));
        } catch (Exception e) {
            return fail(e);
        }
    }

    @PostMapping("/customers")
    public Result<CrmCustomer> createCustomer(@RequestBody CrmCustomer customer, HttpServletRequest request) {
        try {
            return Result.success(crmService.createCustomer(customer, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PutMapping("/customers/{id}")
    public Result<Void> updateCustomer(@PathVariable Long id, @RequestBody CrmCustomer customer, HttpServletRequest request) {
        try {
            customer.setId(id);
            crmService.updateCustomer(customer, crmAuth.requireUser(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PutMapping("/customers/{id}/close")
    public Result<Void> closeCustomer(@PathVariable Long id, HttpServletRequest request) {
        try {
            crmService.closeCustomer(id, crmAuth.requireUser(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PostMapping("/customers/merge")
    public Result<Void> merge(@RequestBody Map<String, Long> body, HttpServletRequest request) {
        try {
            crmService.mergeCustomers(body.get("fromId"), body.get("toId"), crmAuth.requireManager(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @GetMapping("/customers/{id}/contacts")
    public Result<List<CrmContact>> contacts(@PathVariable Long id, HttpServletRequest request) {
        try {
            return Result.success(crmService.listContacts(id, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PostMapping("/contacts")
    public Result<CrmContact> createContact(@RequestBody CrmContact contact, HttpServletRequest request) {
        try {
            return Result.success(crmService.createContact(contact, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PutMapping("/contacts/{id}")
    public Result<Void> updateContact(@PathVariable Long id, @RequestBody CrmContact contact, HttpServletRequest request) {
        try {
            contact.setId(id);
            crmService.updateContact(contact, crmAuth.requireUser(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @GetMapping("/inquiries")
    public Result<PageResult<List<CrmInquiry>>> inquiries(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Integer unassigned,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer closed,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {
        try {
            return Result.success(crmService.listInquiries(keyword, country, source, stage, ownerId, unassigned, customerId, closed, dateFrom, dateTo, page, pageSize, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @GetMapping("/inquiries/{id}")
    public Result<CrmInquiry> inquiry(@PathVariable Long id, HttpServletRequest request) {
        try {
            return Result.success(crmService.getInquiry(id, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PostMapping("/inquiries")
    public Result<CrmInquiry> createInquiry(@RequestBody CrmInquiry inquiry, HttpServletRequest request) {
        try {
            return Result.success(crmService.createInquiry(inquiry, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PutMapping("/inquiries/{id}")
    public Result<Void> updateInquiry(@PathVariable Long id, @RequestBody CrmInquiry inquiry, HttpServletRequest request) {
        try {
            inquiry.setId(id);
            crmService.updateInquiry(inquiry, crmAuth.requireUser(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PutMapping("/inquiries/{id}/stage")
    public Result<Void> updateStage(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            crmService.updateInquiryStage(id, body.get("stage"), crmAuth.requireUser(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PutMapping("/inquiries/{id}/owner")
    public Result<Void> assignOwner(@PathVariable Long id, @RequestBody Map<String, Long> body, HttpServletRequest request) {
        try {
            crmService.assignInquiryOwner(id, body.get("ownerId"), crmAuth.requireManager(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @GetMapping("/inquiries/website/{websiteInquiryId}")
    public Result<CrmInquiry> byWebsite(@PathVariable Long websiteInquiryId,
                                        @RequestParam(required = false) String email,
                                        HttpServletRequest request) {
        try {
            return Result.success(crmService.findByWebsiteInquiry(websiteInquiryId, email, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PutMapping("/inquiries/{id}/close")
    public Result<Void> closeInquiry(@PathVariable Long id, HttpServletRequest request) {
        try {
            crmService.closeInquiry(id, crmAuth.requireUser(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @GetMapping("/follow-ups")
    public Result<List<CrmFollowUp>> followUps(@RequestParam(required = false) Long customerId,
                                               @RequestParam(required = false) Long inquiryId,
                                               HttpServletRequest request) {
        try {
            return Result.success(crmService.listFollowUps(customerId, inquiryId, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PostMapping("/follow-ups")
    public Result<CrmFollowUp> createFollowUp(@RequestBody CrmFollowUp followUp, HttpServletRequest request) {
        try {
            return Result.success(crmService.createFollowUp(followUp, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @GetMapping("/orders")
    public Result<PageResult<List<CrmOrder>>> orders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String incoterm,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {
        try {
            return Result.success(crmService.listOrders(keyword, incoterm, ownerId, customerId, page, pageSize, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @GetMapping("/orders/{id}")
    public Result<CrmOrder> order(@PathVariable Long id, HttpServletRequest request) {
        try {
            return Result.success(crmService.getOrder(id, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PostMapping("/orders")
    public Result<CrmOrder> createOrder(@RequestBody CrmOrder order, HttpServletRequest request) {
        try {
            return Result.success(crmService.createOrder(order, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PutMapping("/orders/{id}")
    public Result<Void> updateOrder(@PathVariable Long id, @RequestBody CrmOrder order, HttpServletRequest request) {
        try {
            order.setId(id);
            crmService.updateOrder(order, crmAuth.requireUser(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @DeleteMapping("/orders/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id, HttpServletRequest request) {
        try {
            crmService.deleteOrder(id, crmAuth.requireUser(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @GetMapping("/after-sales/stats")
    public Result<?> afterSaleStats(HttpServletRequest request) {
        try {
            return Result.success(crmService.afterSaleStats(crmAuth.requireUser(request)));
        } catch (Exception e) {
            return fail(e);
        }
    }

    @GetMapping("/after-sales")
    public Result<PageResult<List<CrmAfterSale>>> afterSales(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer resolved,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {
        try {
            return Result.success(crmService.listAfterSales(keyword, resolved, ownerId, customerId, page, pageSize, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @GetMapping("/after-sales/{id}")
    public Result<CrmAfterSale> afterSale(@PathVariable Long id, HttpServletRequest request) {
        try {
            return Result.success(crmService.getAfterSale(id, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PostMapping("/after-sales")
    public Result<CrmAfterSale> createAfterSale(@RequestBody CrmAfterSale item, HttpServletRequest request) {
        try {
            return Result.success(crmService.createAfterSale(item, crmAuth.requireUser(request)));
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @PutMapping("/after-sales/{id}")
    public Result<Void> updateAfterSale(@PathVariable Long id, @RequestBody CrmAfterSale item, HttpServletRequest request) {
        try {
            item.setId(id);
            crmService.updateAfterSale(item, crmAuth.requireUser(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @DeleteMapping("/after-sales/{id}")
    public Result<Void> deleteAfterSale(@PathVariable Long id, HttpServletRequest request) {
        try {
            crmService.deleteAfterSale(id, crmAuth.requireUser(request));
            return Result.success();
        } catch (Exception e) {
            return (Result) fail(e);
        }
    }

    @GetMapping("/import/template")
    public void template(HttpServletRequest request, HttpServletResponse response) {
        try {
            crmAuth.requireUser(request);
            crmService.downloadTemplate(response);
        } catch (Throwable e) {
            writeDownloadError(response, e);
        }
    }

    @PostMapping("/import/preview")
    public Result<List<CrmImportRow>> preview(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            crmAuth.requireManager(request);
            return Result.success(crmService.previewImport(file));
        } catch (Throwable e) {
            return (Result) fail(e instanceof Exception ? (Exception) e : new RuntimeException(e.getMessage(), e));
        }
    }

    @PostMapping("/import/commit")
    public Result<?> commit(@RequestBody List<CrmImportRow> rows, HttpServletRequest request) {
        try {
            return Result.success(crmService.commitImport(rows, crmAuth.requireManager(request)));
        } catch (Exception e) {
            return fail(e);
        }
    }

    @GetMapping("/export")
    public void export(@RequestParam(defaultValue = "customers") String type,
                       HttpServletRequest request, HttpServletResponse response) {
        try {
            crmService.exportExcel(type, crmAuth.requireUser(request), response);
        } catch (Throwable e) {
            writeDownloadError(response, e);
        }
    }

    private void writeDownloadError(HttpServletResponse response, Throwable e) {
        try {
            if (!response.isCommitted()) {
                response.reset();
                int status = 500;
                if (e instanceof CrmAuth.AuthException) {
                    status = ((CrmAuth.AuthException) e).getCode();
                }
                response.setStatus(status);
                response.setContentType("application/json;charset=UTF-8");
                String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                msg = msg.replace("\\", "\\\\").replace("\"", "'");
                response.getWriter().write("{\"code\":" + status + ",\"message\":\"" + msg + "\"}");
            }
        } catch (Exception ignored) {
        }
    }
}
