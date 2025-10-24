package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class DetalleDocumentoRequestDto {
    private String numero;
    private String fechaEmision;  //yyyy-MM-dd
    private String fechaVencimiento;
    private String origen;
    private Integer documentoId;
    private Integer viajeroId;
}
