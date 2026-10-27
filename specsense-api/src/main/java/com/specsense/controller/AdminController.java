package com.specsense.controller;

import com.specsense.mapper.AdminUserMapper;
import com.specsense.model.entity.AdminUser;
import com.specsense.model.vo.Result;
import com.specsense.model.vo.LoginVO;
import com.specsense.model.dto.LoginRequest;
import com.specsense.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminUserMapper adminUserMapper;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginRequest request) {
        try {
            LoginVO loginVO = adminService.login(request.getUsername(), request.getPassword());
            return Result.success(loginVO);
        } catch (Exception e) {
            return Result.error(401, e.getMessage());
        }
    }

    @PostMapping("/password-reset-request")
    public Result<Void> requestReset(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (username == null || username.trim().isEmpty()) {
            return Result.error(400, "请输入账号");
        }
        AdminUser user = adminUserMapper.findByUsername(username.trim());
        if (user != null) {
            adminUserMapper.updateResetRequested(user.getId(), 1);
        }
        return Result.success();
    }
}
