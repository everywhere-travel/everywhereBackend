package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductoResponse {

    private int id;
    private String codigo;
    private String descripcion;
    private String tipo;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
}
