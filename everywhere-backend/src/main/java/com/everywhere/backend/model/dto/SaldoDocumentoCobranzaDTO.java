package com.everywhere.backend.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaldoDocumentoCobranzaDTO {
    private Long documentoCobranzaId;
    private String documentoCobranzaNumero;
    private BigDecimal totalDeuda;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
}
