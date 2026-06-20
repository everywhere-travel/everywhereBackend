package com.everywhere.backend.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AsientoContableResponseDTO {

    private Integer id;

    private LocalDate fecha;

    private String glosa;

    private String origen;

    private Integer origenId;

    private String moneda;

    private BigDecimal totalDebe;

    private BigDecimal totalHaber;

    private Boolean anulado;

    private Boolean generadoAutomaticamente;

    private LocalDateTime creado;

    private LocalDateTime actualizado;

    private List<DetalleAsientoContableResponseDTO> detalles;
}