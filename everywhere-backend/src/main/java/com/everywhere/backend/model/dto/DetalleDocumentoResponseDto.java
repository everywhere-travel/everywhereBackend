package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class DetalleDocumentoResponseDto {
    private Integer id;
    private String numero;
    private String fechaEmision;  //yyyy-MM-dd
    private String fechaVencimiento;
    private String origen;
    private DocumentoResponseDto documento;
    private ViajeroResponseDTO viajero;
}
