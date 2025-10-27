package com.everywhere.backend.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetalleLiquidacionRequestDTO {
    private String ticket;
    private BigDecimal costoTicket;
    private BigDecimal cargoServicio;
    private BigDecimal valorVenta;
    private String facturaCompra;
    private String boletaPasajero;
    private BigDecimal montoDescuento;
    private BigDecimal pagoPaxUSD;
    private BigDecimal pagoPaxPEN;

    private Integer liquidacionId;
    private Integer viajeroId;
    private Integer productoId;
    private Integer proveedorId;
    private Integer operadorId;
}