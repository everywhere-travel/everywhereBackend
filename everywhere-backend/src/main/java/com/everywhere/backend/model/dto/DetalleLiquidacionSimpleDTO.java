package com.everywhere.backend.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DetalleLiquidacionSimpleDTO {

    private Integer id;
    private String ticket;
    private BigDecimal costoTicket;
    private BigDecimal cargoServicio;
    private BigDecimal valorVenta;
    private String facturaCompra;
    private String boletaPasajero;
    private BigDecimal montoDescuento;
    private BigDecimal pagoPaxUSD;
    private BigDecimal pagoPaxPEN;
    private LocalDateTime creado;
    private LocalDateTime actualizado;

    // Solo las relaciones del detalle (SIN la liquidación)
    private ViajeroResponseDTO viajero;
    private ProductoResponseDTO producto;
    private ProveedorResponseDTO proveedor;
    private OperadorResponseDTO operador;
}
