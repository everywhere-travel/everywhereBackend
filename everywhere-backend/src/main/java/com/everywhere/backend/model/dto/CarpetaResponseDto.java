package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarpetaResponseDTO {

    private int id;
    private String nombre;
    private String descripcion;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private int nivel;
    private Integer carpetaPadreId;

}
