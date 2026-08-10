package com.everywhere.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarpetaItemDTO {
    private Long id;
    /** "cotizacion" | "liquidacion" | "recibo" | "documento-cobranza" */
    private String tipo;
    private String numero;
    private LocalDate fecha;
    private String descripcion;
}
