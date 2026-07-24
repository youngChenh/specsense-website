package com.specsense.service.impl;

import com.specsense.mapper.ContactInquiryMapper;
import com.specsense.model.entity.ContactInquiry;
import com.specsense.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactInquiryMapper contactInquiryMapper;

    @Override
    public boolean submit(ContactInquiry inquiry) {
        return contactInquiryMapper.insert(inquiry) > 0;
    }

    @Override
    public List<ContactInquiry> getAll() {
        return contactInquiryMapper.findAll();
    }

    @Override
    public ContactInquiry getById(Long id) {
        return contactInquiryMapper.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        return contactInquiryMapper.deleteById(id) > 0;
    }
}
