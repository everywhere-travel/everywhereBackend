package com.everywhere.backend.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleAsientoContableRequestDTO {

    private Integer cuentaId;

    private BigDecimal debe;

    private BigDecimal haber;
}