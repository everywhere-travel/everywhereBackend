package com.everywhere.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * DTO for {@link com.everywhere.backend.model.entity.Proveedor}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponseDto implements Serializable {
    int id;
    String nombre;
    LocalDateTime creado;
    LocalDateTime actualizado;
}