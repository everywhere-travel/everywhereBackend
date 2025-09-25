package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;
import com.everywhere.backend.model.entity.DetalleDocumento;
import com.everywhere.backend.model.entity.Documento;
import com.everywhere.backend.model.entity.Viajero;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DetalleDocumentoMapper {

    private final DocumentoMapper documentoMapper;
    private final ViajeroMapper viajeroMapper;

    // DTO → Entity
    public DetalleDocumento toEntity(DetalleDocumentoRequestDto dto) {
        DetalleDocumento detalle = new DetalleDocumento();
        detalle.setNumero(dto.getNumero());
        detalle.setFechaEmision(LocalDate.parse(dto.getFechaEmision()));
        detalle.setFechaVencimiento(LocalDate.parse(dto.getFechaVencimiento()));
        detalle.setOrigen(dto.getOrigen());

        Documento documento = new Documento();
        documento.setId(dto.getDocumentoId());
        detalle.setDocumento(documento);

        Viajero viajero = new Viajero();
        viajero.setId(dto.getViajeroId());
        detalle.setViajero(viajero);

        return detalle;
    }

    // Entity → DTO
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
}