package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class DetalleCotizacionRequestDto {

    private Integer cantidad;
    private Integer unidad;
    private String descripcion;
}
