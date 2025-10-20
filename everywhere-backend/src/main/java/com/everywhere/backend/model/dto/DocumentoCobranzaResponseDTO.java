package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentoCobranzaResponseDTO {
    
    private Long id;
    private String numero;
    private LocalDateTime fechaEmision;
    private String observaciones;
    private String fileVenta;
    private Double costoEnvio;
    private String moneda;
    
    // Información de relaciones
    private Integer cotizacionId;
    private String codigoCotizacion;  // Número de cotización
    private Integer personaId;
    private Integer sucursalId;
    private Integer formaPagoId;
    
    // Información básica para mostrar
    private String clienteNombre;     // Nombre de la persona
    private String sucursalDescripcion;
    private String formaPagoDescripcion;
}