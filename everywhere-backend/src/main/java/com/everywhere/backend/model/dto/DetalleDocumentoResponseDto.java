package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class DetalleDocumentoResponseDTO {
    private Integer id;
    private String numero;
    private String fechaEmision;  //yyyy-MM-dd
    private String fechaVencimiento;
    private String origen;
    private DocumentoResponseDTO documento;
    private ViajeroResponseDTO viajero;
}
