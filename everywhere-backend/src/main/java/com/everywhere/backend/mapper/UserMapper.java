package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    /**
     * Convierte LoginDTO a User entity (para autenticación)
     */
    public User toUserEntity(LoginDTO loginDTO) {
        return modelMapper.map(loginDTO, User.class);
    }

    /**
     * Convierte User entity a AuthResponseDTO (respuesta de login con JWT)
     */
    public AuthResponseDTO toAuthResponseDTO(User user, String token) {
        AuthResponseDTO authResponseDTO = new AuthResponseDTO();

        authResponseDTO.setId(user.getId());
        authResponseDTO.setToken(token);

        // Obtener rol como enum para acceder a permisos
        Role role = Role.fromName(user.getRole().getName());
        authResponseDTO.setRole(role.getName());

        // Cargar permisos desde el rol, no desde el usuario
        authResponseDTO.setPermissions(role.getModulePermissions());

        // Usar email como nombre temporal
        authResponseDTO.setName(user.getNombre());

        authResponseDTO.setPermissions(role.getModulePermissions());

        return authResponseDTO;
    }


    /**
     * Convierte User básico a DTO simple
     */
    public UserBasicDTO toUserBasicDTO(User user) {
        UserBasicDTO userBasicDTO = new UserBasicDTO();
        userBasicDTO.setId(user.getId());
        userBasicDTO.setEmail(user.getEmail());
        userBasicDTO.setName(user.getNombre()); // Usar email como identificador
        return userBasicDTO;
    }
}
