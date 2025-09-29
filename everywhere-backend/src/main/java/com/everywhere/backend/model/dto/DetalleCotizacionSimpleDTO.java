package com.everywhere.backend.model.dto;

import com.everywhere.backend.model.entity.Categoria;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.model.entity.Proveedor;
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
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private BigDecimal comision;

    // Relaciones sin la cotización para evitar referencia circular
    private Categoria categoria;
    private Producto producto;
    private Proveedor proveedor;
}