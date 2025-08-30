package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LiquidacionResponseDTO {

    private Integer id;
    private String numero;
    private LocalDate fechaCompra;
    private LocalDate fechaVencimiento;
    private String destino;
    private Integer numeroPasajeros;
    private String observacion;
    private LocalDateTime creado;
    private LocalDateTime actualizado;

    // private CotizacionResponseDTO cotizacion;
    private ProductoResponse producto;
    private FormaPagoResponseDTO formaPago;
}
