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
    private String nombre;
    private String nombreJuridico;
    private Integer ruc;
}