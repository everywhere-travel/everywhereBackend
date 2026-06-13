package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Integer id;
    private String nombre;
    private String email;
    private RoleResponseDTO role;
    private SucursalResponseDTO sucursal;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
}
