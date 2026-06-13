package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionRequestDTO {

    // Debe seguir el formato MODULO:ACCION (ej: "CLIENTES:READ", "ALL_MODULES:DELETE")
    @NotBlank(message = "El nombre del permiso es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Pattern(
        regexp = "^[A-Z_]+:[A-Z_]+$",
        message = "El nombre debe tener el formato MODULO:ACCION (ej: CLIENTES:READ)"
    )
    private String name;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String description;
}
