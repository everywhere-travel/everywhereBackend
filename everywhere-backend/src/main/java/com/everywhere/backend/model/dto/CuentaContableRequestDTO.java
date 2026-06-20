package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class CuentaContableRequestDTO {

    private String codigo;

    private String nombre;

    private String tipo;

    private Boolean activo;
}