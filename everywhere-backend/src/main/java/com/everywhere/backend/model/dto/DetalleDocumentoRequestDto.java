package com.everywhere.backend.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DetalleDocumentoRequestDto {
    private String numero;
    private LocalDate fechaEmision;  //yyyy-MM-dd
    private LocalDate fechaVencimiento;
    private String origen;
    private Integer documentoId;
    private Integer personaNaturalId;
}