package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class ProveedorRequestDTO {
    private String nombre;
    private String nombreJuridico;
    private Integer ruc;
}