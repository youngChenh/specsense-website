package com.specsense.model.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private String username;
    private String role;

    public LoginVO(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }
}
