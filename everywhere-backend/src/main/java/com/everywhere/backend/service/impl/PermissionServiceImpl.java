package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.dto.PermissionRequestDTO;
import com.everywhere.backend.model.dto.PermissionResponseDTO;
import com.everywhere.backend.model.entity.Permission;
import com.everywhere.backend.repository.PermissionRepository;
import com.everywhere.backend.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponseDTO> findAll() {
        return permissionRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponseDTO findById(Integer id) {
        return toResponseDTO(findOrThrow(id));
    }

    @Override
    @Transactional
    public PermissionResponseDTO create(PermissionRequestDTO request) {
        if (permissionRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Ya existe un permiso con el nombre: " + request.getName());
        }
        Permission permission = new Permission();
        permission.setName(request.getName().toUpperCase().trim());
        permission.setDescription(request.getDescription());
        return toResponseDTO(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public PermissionResponseDTO update(Integer id, PermissionRequestDTO request) {
        Permission permission = findOrThrow(id);
        permission.setName(request.getName().toUpperCase().trim());
        permission.setDescription(request.getDescription());
        return toResponseDTO(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permiso no encontrado con ID: " + id);
        }
        permissionRepository.deleteById(id);
    }

    // ---------- helpers ----------

    private Permission findOrThrow(Integer id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));
    }

    private PermissionResponseDTO toResponseDTO(Permission permission) {
        PermissionResponseDTO dto = new PermissionResponseDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setDescription(permission.getDescription());
        dto.setCreatedAt(permission.getCreatedAt());
        dto.setUpdatedAt(permission.getUpdatedAt());
        return dto;
    }
}
