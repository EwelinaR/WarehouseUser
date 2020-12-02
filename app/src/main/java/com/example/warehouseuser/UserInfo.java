package com.example.warehouseuser;

import java.util.List;

public class UserInfo {

    private final List<String> authorities;

    public UserInfo(List<String> authorities) {
        this.authorities = authorities;
    }

    public boolean isManager() {
        return authorities.contains("ROLE_MANAGER");
    }
}
