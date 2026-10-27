package com.specsense.controller;

import com.specsense.crm.CrmAuth;
import com.specsense.mapper.AdminUserMapper;
import com.specsense.model.dto.AdminUserDTO;
import com.specsense.model.entity.AdminUser;
import com.specsense.model.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/crm/users")
public class CrmUserController {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private CrmAuth crmAuth;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AdminUserDTO toDto(AdminUser user) {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setResetRequested(user.getResetRequested());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    @GetMapping
    public Result<List<AdminUserDTO>> list(@RequestParam(required = false) Boolean all, HttpServletRequest request) {
        try {
            CrmAuth.User current = crmAuth.requireUser(request);
            List<AdminUser> users = Boolean.TRUE.equals(all) && current.isManager()
                    ? adminUserMapper.findAll()
                    : adminUserMapper.findActive();
            if (!current.isManager()) {
                List<AdminUserDTO> self = new ArrayList<AdminUserDTO>();
                for (AdminUser u : users) {
                    if (u.getId().equals(current.getUserId())) {
                        self.add(toDto(u));
                    }
                }
                return Result.success(self);
            }
            List<AdminUserDTO> dtos = new ArrayList<AdminUserDTO>();
            for (AdminUser u : users) {
                dtos.add(toDto(u));
            }
            return Result.success(dtos);
        } catch (CrmAuth.AuthException e) {
            return Result.error(e.getCode(), e.getMessage());
        }
    }

    @PostMapping
    public Result<Void> create(@RequestBody AdminUserDTO body, HttpServletRequest request) {
        try {
            crmAuth.requireManager(request);
            if (body.getUsername() == null || body.getUsername().trim().isEmpty()) {
                return Result.error(400, "用户名不能为空");
            }
            if (adminUserMapper.findByUsername(body.getUsername()) != null) {
                return Result.error(400, "用户名已存在");
            }
            AdminUser user = new AdminUser();
            user.setUsername(body.getUsername().trim());
            user.setDisplayName(body.getDisplayName());
            user.setRole(body.getRole() == null ? "sales" : body.getRole());
            user.setStatus(body.getStatus() == null ? 1 : body.getStatus());
            user.setResetRequested(0);
            String raw = body.getPassword() == null || body.getPassword().isEmpty() ? "123456" : body.getPassword();
            user.setPasswordHash(passwordEncoder.encode(raw));
            adminUserMapper.insert(user);
            return Result.success();
        } catch (CrmAuth.AuthException e) {
            return Result.error(e.getCode(), e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AdminUserDTO body, HttpServletRequest request) {
        try {
            crmAuth.requireManager(request);
            AdminUser user = adminUserMapper.findById(id);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }
            user.setDisplayName(body.getDisplayName());
            user.setRole(body.getRole());
            user.setStatus(body.getStatus());
            adminUserMapper.updateProfile(user);
            return Result.success();
        } catch (CrmAuth.AuthException e) {
            return Result.error(e.getCode(), e.getMessage());
        }
    }

    @PutMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id, HttpServletRequest request) {
        try {
            CrmAuth.User current = crmAuth.requireManager(request);
            if (current.getUserId().equals(id)) {
                return Result.error(400, "不能停用当前账号");
            }
            adminUserMapper.updateStatus(id, 0);
            return Result.success();
        } catch (CrmAuth.AuthException e) {
            return Result.error(e.getCode(), e.getMessage());
        }
    }

    @PutMapping("/{id}/enable")
    public Result<Void> enable(@PathVariable Long id, HttpServletRequest request) {
        try {
            crmAuth.requireManager(request);
            adminUserMapper.updateStatus(id, 1);
            return Result.success();
        } catch (CrmAuth.AuthException e) {
            return Result.error(e.getCode(), e.getMessage());
        }
    }

    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            crmAuth.requireManager(request);
            String password = body.get("password");
            if (password == null || password.length() < 6) {
                return Result.error(400, "新密码至少6位");
            }
            adminUserMapper.updatePassword(id, passwordEncoder.encode(password));
            return Result.success();
        } catch (CrmAuth.AuthException e) {
            return Result.error(e.getCode(), e.getMessage());
        }
    }
}
