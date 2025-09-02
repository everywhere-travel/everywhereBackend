package com.everywhere.backend.model.dto;

import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.model.entity.Cotizacion;
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
    private Cotizacion cotizacion;
    private Carpeta carpeta;
}
