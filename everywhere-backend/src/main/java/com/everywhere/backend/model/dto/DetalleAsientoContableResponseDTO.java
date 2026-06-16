package com.everywhere.backend.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DetalleAsientoContableResponseDTO {

    private Integer id;

    private Integer cuentaId;

    private String cuentaCodigo;

    private String cuentaNombre;

    private BigDecimal debe;

    private BigDecimal haber;

    private LocalDateTime creado;

    private LocalDateTime actualizado;
}