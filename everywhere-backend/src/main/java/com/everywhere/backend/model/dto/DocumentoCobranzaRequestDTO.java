package com.everywhere.backend.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentoCobranzaRequestDTO {
    
    private String codigoCotizacion;
    private LocalDateTime fechaEmision;
    private String clienteNombre;
    private String clienteTelefono;
    private String clienteDocumento;
    private String clienteDireccion; 
    private String sucursalDescripcion;
    private String puntoCompra;
    private String moneda;
    private String formaPago;
    private String observaciones;
    
    // Campos para PDF
    private String fileVenta;
    private BigDecimal costoEnvio;
    
    private BigDecimal subtotal;
    private BigDecimal total;
    private String importeEnLetras;
    
    private List<DetalleDocumentoCobranza> detalles;
    
    @Data
    public static class DetalleDocumentoCobranza {
        private Integer cantidad;
        private String codigoProducto;
        private String descripcion;
        private BigDecimal precioUnitario; 
    }
}