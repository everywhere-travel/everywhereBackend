package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDate;

@Data
public class LiquidacionRequestDTO {
    @Size(max = 100, message = "El número no puede superar 100 caracteres")
    private String numero;
    private LocalDate fechaCompra; 
    @Size(max = 500, message = "El destino no puede superar 500 caracteres")
    private String destino;
    @Min(value = 0, message = "El número de pasajeros no puede ser negativo")
    @Max(value = 1000, message = "El número de pasajeros no puede superar 1000")
    private Integer numeroPasajeros; 

    private Integer cotizacionId;
    private Integer productoId;
    private Integer formaPagoId;
    private Integer carpetaId;
}