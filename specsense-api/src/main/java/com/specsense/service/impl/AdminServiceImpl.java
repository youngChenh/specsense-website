package com.specsense.service.impl;

import com.specsense.config.JwtConfig;
import com.specsense.mapper.AdminUserMapper;
import com.specsense.model.entity.AdminUser;
import com.specsense.model.vo.LoginVO;
import com.specsense.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private JwtConfig jwtConfig;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginVO login(String username, String password) {
        AdminUser user = adminUserMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtConfig.generateToken(username, user.getId(), user.getRole());
        return new LoginVO(token, username, user.getRole());
    }

    @Override
    public AdminUser getById(Long id) {
        return adminUserMapper.findById(id);
    }

    @Override
    public boolean save(AdminUser user) {
        if (user.getPasswordHash() != null && !user.getPasswordHash().startsWith("$2")) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        return adminUserMapper.insert(user) > 0;
    }

    @Override
    public boolean update(AdminUser user) {
        if (user.getPasswordHash() != null && !user.getPasswordHash().startsWith("$2")) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        return adminUserMapper.update(user) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return adminUserMapper.deleteById(id) > 0;
    }
}
