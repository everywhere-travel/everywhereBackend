package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;
    private final RolePermissionRepository rolePermissionRepository;

    // Convierte LoginDTO a User entity (para autenticación)
    public User toUserEntity(LoginDTO loginDTO) {
        return modelMapper.map(loginDTO, User.class);
    }

    // Convierte User entity a AuthResponseDTO (respuesta de login con JWT)
    public AuthResponseDTO toAuthResponseDTO(User user, String token) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        
        if (user.getRole() == null) {
            throw new IllegalArgumentException("El usuario no tiene un rol asignado");
        }

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();

        authResponseDTO.setId(user.getId());
        authResponseDTO.setToken(token);
        
        authResponseDTO.setRole(user.getRole().getName());
        authResponseDTO.setName(user.getNombre()); // Usar email como nombre temporal

        // Cargar permisos desde BD en formato plano: "MODULO:ACCION"
        Set<String> permissions = rolePermissionRepository
                .findPermissionNamesByRoleId(user.getRole().getId());
        authResponseDTO.setPermissions(permissions);

        return authResponseDTO;
    }

    // Convierte User básico a DTO simple
    public UserBasicDTO toUserBasicDTO(User user) {
        UserBasicDTO userBasicDTO = new UserBasicDTO();
        userBasicDTO.setId(user.getId());
        userBasicDTO.setEmail(user.getEmail());
        userBasicDTO.setName(user.getNombre()); // Usar email como identificador
        return userBasicDTO;
    }

    public UserProfileDTO toUserProfileDTO(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        UserProfileDTO profile = new UserProfileDTO();
        profile.setId(user.getId());
        profile.setName(user.getNombre());
        profile.setEmail(user.getEmail());
        profile.setRole(user.getRole() != null ? user.getRole().getName() : null);

        if (user.getSucursal() != null) {
            SucursalResponseDTO sucursal = modelMapper.map(user.getSucursal(), SucursalResponseDTO.class);
            profile.setSucursal(sucursal);
        }

        return profile;
    }

    public UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setNombre(user.getNombre());
        dto.setEmail(user.getEmail());
        dto.setCreado(user.getCreado());
        dto.setActualizado(user.getActualizado());

        if (user.getRole() != null) {
            RoleResponseDTO roleDto = new RoleResponseDTO();
            roleDto.setId(user.getRole().getId());
            roleDto.setName(user.getRole().getName());
            roleDto.setCreatedAt(user.getRole().getCreatedAt());
            roleDto.setUpdatedAt(user.getRole().getUpdatedAt());
            roleDto.setPermissions(rolePermissionRepository.findPermissionNamesByRoleId(user.getRole().getId()));
            dto.setRole(roleDto);
        }

        if (user.getSucursal() != null) {
            dto.setSucursal(modelMapper.map(user.getSucursal(), SucursalResponseDTO.class));
        }

        return dto;
    }
}