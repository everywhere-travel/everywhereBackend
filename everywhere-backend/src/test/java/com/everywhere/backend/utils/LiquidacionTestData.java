package com.everywhere.backend.utils;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LiquidacionTestData {

    public static LiquidacionRequestDTO getValidLiquidacionRequestDTO() {
        LiquidacionRequestDTO dto = new LiquidacionRequestDTO();
        dto.setNumero("LIQ-2023-001");
        dto.setFechaCompra(LocalDate.of(2023, 10, 15));
        dto.setDestino("Cancun");
        dto.setNumeroPasajeros(2);
        dto.setCotizacionId(1);
        dto.setProductoId(1);
        dto.setFormaPagoId(1);
        return dto;
    }

    public static LiquidacionResponseDTO getValidLiquidacionResponseDTO() {
        LiquidacionResponseDTO dto = new LiquidacionResponseDTO();
        dto.setId(1);
        dto.setNumero("LIQ-2023-001");
        dto.setFechaCompra(LocalDate.of(2023, 10, 15));
        dto.setDestino("Cancun");
        dto.setNumeroPasajeros(2);
        dto.setCreado(LocalDateTime.now());
        dto.setActualizado(LocalDateTime.now());
        return dto;
    }
}
