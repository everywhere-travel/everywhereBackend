package com.everywhere.backend.utils;

import com.everywhere.backend.model.dto.PermissionResponseDTO;
import com.everywhere.backend.model.dto.RolePermissionRequestDTO;
import com.everywhere.backend.model.dto.RoleRequestDTO;
import com.everywhere.backend.model.dto.RoleResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public class RoleTestData {

    public static RoleRequestDTO getValidRoleRequest() {
        RoleRequestDTO request = new RoleRequestDTO();
        request.setName("ADMIN");
        return request;
    }

    public static RoleResponseDTO getValidRoleResponse() {
        RoleResponseDTO response = new RoleResponseDTO();
        response.setId(1);
        response.setName("ADMIN");
        PermissionResponseDTO permission = new PermissionResponseDTO();
        permission.setId(1);
        permission.setName("CLIENTES:READ");
        response.setPermissions(List.of(permission));
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        return response;
    }

    public static RolePermissionRequestDTO getValidRolePermissionRequest() {
        RolePermissionRequestDTO request = new RolePermissionRequestDTO();
        request.setPermissionId(1);
        return request;
    }
}
