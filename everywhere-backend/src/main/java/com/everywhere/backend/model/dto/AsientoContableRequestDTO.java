package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AsientoContableRequestDTO {

    private LocalDate fecha;

    private String glosa;

    private String origen;

    private Integer origenId;

    private String moneda;

    private Boolean generadoAutomaticamente;

    private List<DetalleAsientoContableRequestDTO> detalles;
}