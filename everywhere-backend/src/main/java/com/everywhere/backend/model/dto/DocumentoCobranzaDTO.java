package com.everywhere.backend.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentoCobranzaDTO {
    
    // Campos manuales
    private String nroSerie;
    private String fileVenta;
    private BigDecimal costoEnvio;
    
    // Campos de cotización
    private LocalDateTime fechaEmision;
    private String clienteEmail;
    private String clienteTelefono;
    private String clienteDireccion;
    private String puntoCompra;
    private String moneda;
    private String formaPago;
    private String observaciones;
    
    // Totales
    private BigDecimal subtotal;
    private BigDecimal total;
    private String importeEnLetras;
    
    // Detalles
    private List<DetalleDocumentoCobranza> detalles;
    
    @Data
    public static class DetalleDocumentoCobranza {
        private Integer cantidad;
        private String codigoProducto;
        private String descripcion;
        private BigDecimal precioUnitario; 
    }
}
