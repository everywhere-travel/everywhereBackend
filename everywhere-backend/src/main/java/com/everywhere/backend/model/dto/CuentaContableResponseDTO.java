package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CuentaContableResponseDTO {

    private Integer id;

    private String codigo;

    private String nombre;

    private String tipo;

    private Boolean activo;

    private LocalDateTime creado;

    private LocalDateTime actualizado;
}