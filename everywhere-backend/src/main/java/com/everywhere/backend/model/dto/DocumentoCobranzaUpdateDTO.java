package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class DocumentoCobranzaUpdateDTO {
    private String fileVenta;
    private Double costoEnvio;
    private String observaciones;
    private Integer detalleDocumentoId;
}