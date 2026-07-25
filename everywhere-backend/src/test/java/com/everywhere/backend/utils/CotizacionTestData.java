package com.everywhere.backend.utils;

import com.everywhere.backend.model.dto.CotizacionRequestDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CotizacionTestData {

    public static CotizacionRequestDto createValidRequestDto() {
        CotizacionRequestDto dto = new CotizacionRequestDto();
        dto.setNombreCotizacion("Paquete Cancún 5 días");
        dto.setCantAdultos(2);
        dto.setCantNinos(0);
        dto.setFechaVencimiento(LocalDateTime.now().plusDays(7));
        dto.setOrigenDestino("LIM-CUN");
        dto.setFechaSalida(LocalDate.now().plusMonths(1));
        dto.setFechaRegreso(LocalDate.now().plusMonths(1).plusDays(5));
        dto.setMoneda("USD");
        dto.setObservacion("Vuelo directo");
        dto.setCounterId(1);
        dto.setFormaPagoId(1);
        dto.setEstadoCotizacionId(1);
        dto.setSucursalId(1);
        dto.setPersonaId(1);
        return dto;
    }

    public static CotizacionResponseDto createValidResponseDto(int id) {
        CotizacionResponseDto dto = new CotizacionResponseDto();
        dto.setId(id);
        dto.setNombreCotizacion("Paquete Cancún 5 días");
        dto.setCodigoCotizacion("COT-2023-0001");
        dto.setCantAdultos(2);
        dto.setCantNinos(0);
        dto.setFechaEmision(LocalDateTime.now());
        dto.setOrigenDestino("LIM-CUN");
        dto.setMoneda("USD");
        return dto;
    }
}
