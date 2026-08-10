package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleResponseDTO {
    private Integer id;
    private String name;
    private List<PermissionResponseDTO> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
