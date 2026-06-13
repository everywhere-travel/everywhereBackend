package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.RoleRequestDTO;
import com.everywhere.backend.model.dto.RoleResponseDTO;

import java.util.List;

public interface RoleService {
    List<RoleResponseDTO> findAll();
    RoleResponseDTO findById(Integer id);
    RoleResponseDTO create(RoleRequestDTO request);
    RoleResponseDTO update(Integer id, RoleRequestDTO request);
    void delete(Integer id);

    // Asignar un permiso a un rol
    RoleResponseDTO addPermission(Integer roleId, Integer permissionId);

    // Quitar un permiso de un rol
    RoleResponseDTO removePermission(Integer roleId, Integer permissionId);
}
