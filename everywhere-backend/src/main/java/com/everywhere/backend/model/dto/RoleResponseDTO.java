package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class RoleResponseDTO {
    private Integer id;
    private String name;
    private Set<String> permissions; // ["CLIENTES:READ", "COTIZACIONES:CREATE", ...]
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
