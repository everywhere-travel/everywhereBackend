package com.everywhere.backend.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentoCobranzaDTO {
    
    // Campos manuales
    private String fileVenta;
    private BigDecimal costoEnvio;
    
    // Campos de cotización
    private String codigoCotizacion;   // Número de cotización
    private LocalDateTime fechaEmision;
    private String clienteNombre;      // Nombre completo del cliente
    private String clienteTelefono;
    private String clienteDocumento;   // DNI o RUC del cliente
    private String clienteDireccion;   // Dirección del cliente
    private String sucursalDescripcion;
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
