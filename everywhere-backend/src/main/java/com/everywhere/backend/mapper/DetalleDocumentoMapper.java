package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;
import com.everywhere.backend.model.entity.DetalleDocumento;
import com.everywhere.backend.model.entity.Documento;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.repository.DocumentoRepository;
import com.everywhere.backend.repository.ViajeroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DetalleDocumentoMapper {

    private final DocumentoMapper documentoMapper;
    private final ViajeroMapper viajeroMapper;

    private final DocumentoRepository documentoRepository;
    private final ViajeroRepository viajeroRepository;

    public DetalleDocumento toEntity(DetalleDocumentoRequestDto dto) {
        DetalleDocumento detalle = new DetalleDocumento();
        updateEntityFromDto(dto, detalle);
        return detalle;
    }

    public DetalleDocumentoResponseDto toDto(DetalleDocumento detalle) {
        DetalleDocumentoResponseDto dto = new DetalleDocumentoResponseDto();
        dto.setId(detalle.getId());
        dto.setNumero(detalle.getNumero());
        dto.setFechaEmision(detalle.getFechaEmision().toString());
        dto.setFechaVencimiento(detalle.getFechaVencimiento().toString());
        dto.setOrigen(detalle.getOrigen());

        dto.setDocumento(detalle.getDocumento() != null
                ? documentoMapper.toDto(detalle.getDocumento())
                : null);

        dto.setViajero(detalle.getViajero() != null
                ? viajeroMapper.toResponseDTO(detalle.getViajero())
                : null);

        return dto;
    }

    public void updateEntityFromDto(DetalleDocumentoRequestDto dto, DetalleDocumento detalle) {
        detalle.setNumero(dto.getNumero());
        detalle.setFechaEmision(LocalDate.parse(dto.getFechaEmision()));
        detalle.setFechaVencimiento(LocalDate.parse(dto.getFechaVencimiento()));
        detalle.setOrigen(dto.getOrigen());

        // ✅ Traer Documento completo desde la BD
        if (dto.getDocumentoId() != null) {
            documentoRepository.findById(dto.getDocumentoId())
                    .ifPresent(detalle::setDocumento);
        }

        // ✅ Traer Viajero completo desde la BD
        if (dto.getViajeroId() != null) {
            viajeroRepository.findById(dto.getViajeroId())
                    .ifPresent(detalle::setViajero);
        }
    }
}
