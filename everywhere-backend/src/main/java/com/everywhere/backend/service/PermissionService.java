package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.PermissionRequestDTO;
import com.everywhere.backend.model.dto.PermissionResponseDTO;

import java.util.List;

public interface PermissionService {
    List<PermissionResponseDTO> findAll();
    PermissionResponseDTO findById(Integer id);
    PermissionResponseDTO create(PermissionRequestDTO request);
    PermissionResponseDTO update(Integer id, PermissionRequestDTO request);
    void delete(Integer id);
}
