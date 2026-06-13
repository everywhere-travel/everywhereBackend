package com.everywhere.backend.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class AuthResponseDTO {
    private Integer id;
    private String token;
    private String name;
    private String role;
    // Formato: ["CLIENTES:READ", "COTIZACIONES:CREATE", "ALL_MODULES:DELETE", ...]
    private Set<String> permissions;
}
