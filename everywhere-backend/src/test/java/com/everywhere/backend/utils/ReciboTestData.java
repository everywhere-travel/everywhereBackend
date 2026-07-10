package com.everywhere.backend.utils;

import com.everywhere.backend.model.dto.ReciboResponseDTO;
import com.everywhere.backend.model.dto.ReciboUpdateDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReciboTestData {

    public static ReciboUpdateDTO getValidReciboUpdateDTO() {
        ReciboUpdateDTO dto = new ReciboUpdateDTO();
        dto.setFechaEmision(LocalDate.now());
        dto.setObservaciones("Pago parcial en ventanilla");
        return dto;
    }

    public static ReciboResponseDTO getValidReciboResponseDTO() {
        ReciboResponseDTO dto = new ReciboResponseDTO();
        dto.setId(1);
        dto.setSerie("R001");
        dto.setCorrelativo(5678);
        dto.setFechaEmision(LocalDate.now());
        dto.setDocumentoCobranzaId(1L);
        dto.setDocumentoCobranzaNumero("F001-1234");
        dto.setSaldoPendienteActual(new BigDecimal("1000.00"));
        return dto;
    }
}
