package com.everywhere.backend.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentoCobranzaResponseDTO {
    
    private Long id;
    private String numero;
    private LocalDateTime fechaEmision;
    private String observaciones;
    private String fileVenta;
    private BigDecimal costoEnvio;
    private String moneda;
    
    // Información de relaciones
    private Integer cotizacionId;
    private String codigoCotizacion; 
    private Integer personaId;
    private Integer sucursalId;
    private Integer formaPagoId;
    
    // Información básica para mostrar
    private String clienteNombre;
    private String clienteDocumento;
    private String tipoDocumentoCliente;
    private String sucursalDescripcion;
    private String formaPagoDescripcion;
    private List<DetalleDocumentoCobranzaResponseDTO> detalles;
}