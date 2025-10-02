package com.everywhere.backend.model.dto;
 
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LiquidacionResponseDTO {

    private Integer id;
    private String numero;
    private LocalDate fechaCompra; 
    private String destino;
    private Integer numeroPasajeros; 
    private LocalDateTime creado;
    private LocalDateTime actualizado;

    private CotizacionResponseDto cotizacion;
    private ProductoResponse producto;
    private FormaPagoResponseDTO formaPago; 
    private CarpetaResponseDto carpeta;
    
}
