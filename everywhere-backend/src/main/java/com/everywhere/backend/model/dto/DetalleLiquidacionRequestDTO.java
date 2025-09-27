package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class DetalleLiquidacionRequestDTO {

    @Size(max = 255)
    private String ticket;

    @DecimalMin(value = "0.0")
    private BigDecimal costoTicket;

    @DecimalMin(value = "0.0")
    private BigDecimal cargoServicio;

    @DecimalMin(value = "0.0")
    private BigDecimal valorVenta;

    @Size(max = 255)
    private String facturaCompra;

    @Size(max = 255)
    private String boletaPasajero;

    @DecimalMin(value = "0.0")
    private BigDecimal montoDescuento;

    @DecimalMin(value = "0.0")
    private BigDecimal pagoPaxUSD;

    @DecimalMin(value = "0.0")
    private BigDecimal pagoPaxPEN;

    @NotNull
    private Integer liquidacionId;

    @NotNull
    private Integer viajeroId;

    @NotNull
    private Integer productoId;

    @NotNull
    private Integer proveedorId;

    @NotNull
    private Integer operadorId;
}
