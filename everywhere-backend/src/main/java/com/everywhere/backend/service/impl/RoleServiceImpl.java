package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.dto.RoleRequestDTO;
import com.everywhere.backend.model.dto.RoleResponseDTO;
import com.everywhere.backend.model.entity.Permission;
import com.everywhere.backend.model.entity.Role;
import com.everywhere.backend.model.entity.RolePermission;
import com.everywhere.backend.repository.PermissionRepository;
import com.everywhere.backend.repository.RolePermissionRepository;
import com.everywhere.backend.repository.RoleRepository;
import com.everywhere.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> findAll() {
        return roleRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDTO findById(Integer id) {
        Role role = findRoleOrThrow(id);
        return toResponseDTO(role);
    }

    @Override
    @Transactional
    public RoleResponseDTO create(RoleRequestDTO request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + request.getName());
        }
        Role role = new Role();
        role.setName(request.getName().toUpperCase().trim());
        return toResponseDTO(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponseDTO update(Integer id, RoleRequestDTO request) {
        Role role = findRoleOrThrow(id);
        role.setName(request.getName().toUpperCase().trim());
        return toResponseDTO(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RoleResponseDTO addPermission(Integer roleId, Integer permissionId) {
        Role role = findRoleOrThrow(roleId);

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + permissionId));

        if (rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            throw new IllegalArgumentException("El rol ya tiene asignado ese permiso");
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermissionRepository.save(rolePermission);

        return toResponseDTO(role);
    }

    @Override
    @Transactional
    public RoleResponseDTO removePermission(Integer roleId, Integer permissionId) {
        findRoleOrThrow(roleId);
        rolePermissionRepository.deleteByRoleIdAndPermissionId(roleId, permissionId);
        return toResponseDTO(findRoleOrThrow(roleId));
    }

    // ---------- helpers ----------

    private Role findRoleOrThrow(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
    }

    private RoleResponseDTO toResponseDTO(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());

        Set<String> permissions = rolePermissionRepository.findPermissionNamesByRoleId(role.getId());
        dto.setPermissions(permissions);

        return dto;
    }
}
