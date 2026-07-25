package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class DocumentoCobranzaUpdateDTO {
    private LocalDate fechaEmision;
    @Size(max = 100, message = "El fileVenta no puede superar 100 caracteres")
    private String fileVenta;
    private Double costoEnvio;
    @Size(max = 10000, message = "Las observaciones no pueden superar 10000 caracteres")
    private String observaciones;
    private Integer detalleDocumentoId;
    private Integer sucursalId;
    private Integer personaJuridicaId;
    private Integer formaPagoId;
}