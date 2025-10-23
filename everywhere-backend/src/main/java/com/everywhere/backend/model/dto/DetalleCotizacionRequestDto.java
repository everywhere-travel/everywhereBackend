package com.everywhere.backend.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleCotizacionRequestDTO {

    private Integer cantidad;
    private Integer unidad;
    private String descripcion;
    private Integer categoria;
    private BigDecimal comision;
    private BigDecimal precioHistorico;
    private Boolean seleccionado;

}
