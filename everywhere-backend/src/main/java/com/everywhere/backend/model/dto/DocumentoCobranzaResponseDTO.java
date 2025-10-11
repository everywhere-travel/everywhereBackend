package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentoCobranzaResponseDTO {
    
    private Long id;
    private String numero;
    private LocalDateTime fechaEmision;
    private String observaciones;
    private String nroSerie;
    private String fileVenta;
    private Double costoEnvio;
    private String moneda;
    
    // Información de relaciones
    private Integer cotizacionId;
    private Integer personaId;
    private Integer sucursalId;
    private Integer formaPagoId;
    
    // Información básica para mostrar
    private String clienteEmail;
    private String sucursalDescripcion;
    private String formaPagoDescripcion;
}