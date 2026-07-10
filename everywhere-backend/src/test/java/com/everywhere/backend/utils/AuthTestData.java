package com.everywhere.backend.utils;

import com.everywhere.backend.model.dto.AuthResponseDTO;
import com.everywhere.backend.model.dto.LoginDTO;

import java.util.Set;

public class AuthTestData {

    public static LoginDTO createValidLoginDTO() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("admin@everywhere.com");
        dto.setPassword("password123");
        return dto;
    }

    public static LoginDTO createInvalidLoginDTO() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("not-an-email");
        dto.setPassword("");
        return dto;
    }

    public static AuthResponseDTO createAuthResponseDTO() {
        AuthResponseDTO response = new AuthResponseDTO();
        response.setId(1);
        response.setName("Admin User");
        response.setToken("eyJhbGciOiJIUzUxMiJ9.mockToken...");
        response.setRole("ADMIN");
        response.setPermissions(Set.of("ALL_MODULES:READ", "ALL_MODULES:CREATE"));
        return response;
    }
}
