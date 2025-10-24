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

    private CotizacionResponseDTO cotizacion;
    private ProductoResponseDTO producto;
    private FormaPagoResponseDTO formaPago; 
    private CarpetaResponseDTO carpeta;
    
}
