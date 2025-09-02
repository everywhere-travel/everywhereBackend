package com.everywhere.backend.model.dto;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.io.Serializable;
import lombok.*;
/**
 * DTO for {@link com.everywhere.backend.model.entity.Proveedor}
 */
@Data
public class ProveedorRequestDto implements Serializable {
    String nombre;
}