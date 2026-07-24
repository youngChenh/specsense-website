package com.specsense.controller;

import com.specsense.model.vo.Result;
import com.specsense.model.vo.LoginVO;
import com.specsense.model.dto.LoginRequest;
import com.specsense.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginRequest request) {
        try {
            LoginVO loginVO = adminService.login(request.getUsername(), request.getPassword());
            return Result.success(loginVO);
        } catch (Exception e) {
            return Result.error(401, e.getMessage());
        }
    }
}
