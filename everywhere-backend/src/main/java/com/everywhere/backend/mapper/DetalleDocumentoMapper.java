package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DetalleDocumentoRequestDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDTO;
import com.everywhere.backend.model.entity.DetalleDocumento; 
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

    public DetalleDocumento toEntity(DetalleDocumentoRequestDTO dto) {
        DetalleDocumento detalle = new DetalleDocumento();
        updateEntityFromDto(dto, detalle);
        return detalle;
    }

    public DetalleDocumentoResponseDTO toDto(DetalleDocumento detalle) {
        DetalleDocumentoResponseDTO dto = new DetalleDocumentoResponseDTO();
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

    public void updateEntityFromDto(DetalleDocumentoRequestDTO dto, DetalleDocumento detalle) {
        detalle.setNumero(dto.getNumero());
        detalle.setFechaEmision(LocalDate.parse(dto.getFechaEmision()));
        detalle.setFechaVencimiento(LocalDate.parse(dto.getFechaVencimiento()));
        detalle.setOrigen(dto.getOrigen());

        if (dto.getDocumentoId() != null) {
            documentoRepository.findById(dto.getDocumentoId())
                    .ifPresent(detalle::setDocumento);
        }

        if (dto.getViajeroId() != null) {
            viajeroRepository.findById(dto.getViajeroId())
                    .ifPresent(detalle::setViajero);
        }
    }
}
