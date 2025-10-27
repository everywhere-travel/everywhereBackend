package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable; 
/**
 * DTO for {@link com.everywhere.backend.model.entity.Proveedor}
 */
@Data
public class ProveedorRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @Size(max = 150, message = "El nombre jurídico no puede superar los 150 caracteres")
    private String nombreJuridico;

    @Min(value = 10000000, message = "El RUC debe tener al menos 8 dígitos")
    private Integer ruc;
}