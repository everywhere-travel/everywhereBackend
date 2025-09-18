package com.everywhere.backend.model.dto;

import lombok.Data;

import java.io.Serializable;
/**
 * DTO for {@link com.everywhere.backend.model.entity.Proveedor}
 */
@Data
public class ProveedorRequestDto implements Serializable {
    String nombre;
}