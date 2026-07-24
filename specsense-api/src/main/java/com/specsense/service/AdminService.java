package com.specsense.service;

import com.specsense.model.entity.AdminUser;
import com.specsense.model.vo.LoginVO;

public interface AdminService {
    LoginVO login(String username, String password);
    AdminUser getById(Long id);
    boolean save(AdminUser user);
    boolean update(AdminUser user);
    boolean deleteById(Long id);
}
