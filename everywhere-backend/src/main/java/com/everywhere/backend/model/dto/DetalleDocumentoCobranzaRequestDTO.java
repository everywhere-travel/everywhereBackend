package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleDocumentoCobranzaRequestDTO {
    
    @Positive(message = "La cantidad debe ser positiva")
    private Integer cantidad;
    
    private String descripcion;
    
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precio;
    
    private Long documentoCobranzaId;
    
    private Integer productoId;
}