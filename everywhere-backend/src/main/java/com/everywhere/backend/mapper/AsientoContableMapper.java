package com.everywhere.backend.mapper;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.everywhere.backend.model.dto.AsientoContableRequestDTO;
import com.everywhere.backend.model.dto.AsientoContableResponseDTO;
import com.everywhere.backend.model.entity.AsientoContable;

@Component
public class AsientoContableMapper {

    private final DetalleAsientoContableMapper detalleMapper;

    public AsientoContableMapper(
            DetalleAsientoContableMapper detalleMapper) {

        this.detalleMapper = detalleMapper;
    }

    public AsientoContable toEntity(
            AsientoContableRequestDTO dto) {

        AsientoContable entity = new AsientoContable();

        entity.setFecha(dto.getFecha());

        entity.setGlosa(dto.getGlosa());

        entity.setOrigen(dto.getOrigen());

        entity.setOrigenId(dto.getOrigenId());

        entity.setMoneda(dto.getMoneda());

        entity.setGeneradoAutomaticamente(
                dto.getGeneradoAutomaticamente());

        entity.setAnulado(false);

        entity.setTotalDebe(BigDecimal.ZERO);

        entity.setTotalHaber(BigDecimal.ZERO);

        return entity;
    }

    public AsientoContableResponseDTO toResponse(
            AsientoContable entity) {

        AsientoContableResponseDTO dto =
                new AsientoContableResponseDTO();

        dto.setId(entity.getId());

        dto.setFecha(entity.getFecha());

        dto.setGlosa(entity.getGlosa());

        dto.setOrigen(entity.getOrigen());

        dto.setOrigenId(entity.getOrigenId());

        dto.setMoneda(entity.getMoneda());

        dto.setTotalDebe(entity.getTotalDebe());

        dto.setTotalHaber(entity.getTotalHaber());

        dto.setAnulado(entity.getAnulado());

        dto.setGeneradoAutomaticamente(
                entity.getGeneradoAutomaticamente());

        dto.setCreado(entity.getCreado());

        dto.setActualizado(entity.getActualizado());

        if (entity.getDetalles() != null) {

            dto.setDetalles(
                    entity.getDetalles()
                            .stream()
                            .map(detalleMapper::toResponse)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}