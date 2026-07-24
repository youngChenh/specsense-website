package com.specsense.service;

import com.specsense.model.entity.ContactInquiry;
import java.util.List;

public interface ContactService {
    boolean submit(ContactInquiry inquiry);
    List<ContactInquiry> getAll();
    ContactInquiry getById(Long id);
    boolean deleteById(Long id);
}
