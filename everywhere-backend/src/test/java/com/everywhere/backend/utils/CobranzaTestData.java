package com.everywhere.backend.utils;

import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaUpdateDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CobranzaTestData {

    public static DocumentoCobranzaUpdateDTO getValidDocumentoCobranzaUpdateDTO() {
        DocumentoCobranzaUpdateDTO dto = new DocumentoCobranzaUpdateDTO();
        dto.setFechaEmision(LocalDate.now());
        dto.setObservaciones("Pago de reserva");
        return dto;
    }

    public static DocumentoCobranzaResponseDTO getValidDocumentoCobranzaResponseDTO() {
        DocumentoCobranzaResponseDTO dto = new DocumentoCobranzaResponseDTO();
        dto.setId(1L);
        dto.setSerie("F001");
        dto.setCorrelativo(1234);
        dto.setFechaEmision(LocalDate.now());
        dto.setTotalDeuda(new BigDecimal("1500.00"));
        dto.setTotalPagado(new BigDecimal("500.00"));
        dto.setSaldoPendiente(new BigDecimal("1000.00"));
        return dto;
    }
}
