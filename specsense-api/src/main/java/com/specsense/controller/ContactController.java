package com.specsense.controller;

import com.specsense.model.vo.Result;
import com.specsense.model.entity.ContactInquiry;
import com.specsense.service.ContactService;
import com.specsense.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public Result<String> submit(@Valid @RequestBody ContactInquiry inquiry) {
        // Save to database
        contactService.submit(inquiry);

        // Send email notification
        try {
            emailService.sendContactInquiry(
                inquiry.getName(),
                inquiry.getEmail(),
                inquiry.getCompany(),
                inquiry.getProduct(),
                inquiry.getMessage()
            );
        } catch (Exception e) {
            // Log error but don't fail the request
            e.printStackTrace();
        }

        return Result.success("Thank you for your inquiry! We will get back to you soon.");
    }
}
