package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.CotizacionRequestDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;
import com.everywhere.backend.model.entity.Cotizacion;

public class CotizacionMapper {

    public static CotizacionResponseDto toResponse(Cotizacion entity) {
        if (entity == null) {
            return null;
        }

        CotizacionResponseDto dto = new CotizacionResponseDto();
        dto.setId(entity.getId());
        dto.setCodigoCotizacion(entity.getCodigoCotizacion());
        dto.setCantAdultos(entity.getCantAdultos());
        dto.setCantNinos(entity.getCantNinos());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setFechaVencimiento(entity.getFechaVencimiento());
        dto.setActualizado(entity.getActualizado());
        dto.setOrigenDestino(entity.getOrigenDestino());
        dto.setFechaSalida(entity.getFechaSalida());
        dto.setFechaRegreso(entity.getFechaRegreso());
        dto.setMoneda(entity.getMoneda());
        dto.setObservacion(entity.getObservacion());

        // Relaciones
        dto.setCounter(entity.getCounter());
        dto.setFormaPago(entity.getFormaPago());
        dto.setEstadoCotizacion(entity.getEstadoCotizacion());

        return dto;
    }

    public static void updateEntityFromRequest(Cotizacion entity, CotizacionRequestDto dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setCantAdultos(dto.getCantAdultos());
        entity.setCantNinos(dto.getCantNinos());
        entity.setFechaVencimiento(dto.getFechaVencimiento());
        entity.setOrigenDestino(dto.getOrigenDestino());
        entity.setFechaSalida(dto.getFechaSalida());
        entity.setFechaRegreso(dto.getFechaRegreso());
        entity.setMoneda(dto.getMoneda());
        entity.setObservacion(dto.getObservacion());
    }
}
