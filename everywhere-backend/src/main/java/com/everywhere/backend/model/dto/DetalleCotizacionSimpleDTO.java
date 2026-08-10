package com.everywhere.backend.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DetalleCotizacionSimpleDTO {

    private int id;
    private Integer cantidad;
    private Integer unidad;
    private String descripcion;
    private BigDecimal precioHistorico;
    private Boolean seleccionado;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private BigDecimal comision;


    private CategoriaResponseDto categoria;
    private ProductoResponseDTO producto;
    private ProveedorResponseDTO proveedor;
    private OperadorResponseDTO operador;
}