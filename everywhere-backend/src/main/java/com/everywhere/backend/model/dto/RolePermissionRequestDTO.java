package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RolePermissionRequestDTO {
    @NotNull(message = "El ID del permiso es obligatorio")
    private Integer permissionId;
}
