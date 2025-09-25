package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DocumentoRequestDto;
import com.everywhere.backend.model.dto.DocumentoResponseDto;
import com.everywhere.backend.model.entity.Documento;
import org.springframework.stereotype.Component;

@Component
public class DocumentoMapper {

    // DTO → Entity
    public static Documento toEntity(DocumentoRequestDto dto) {
        Documento documento = new Documento();
        documento.setTipo(dto.getTipo());
        documento.setDescripcion(dto.getDescripcion());
        return documento;
    }

    // Entity → DTO
    public static DocumentoResponseDto toDto(Documento documento) {
        DocumentoResponseDto dto = new DocumentoResponseDto();
        dto.setId(documento.getId());
        dto.setTipo(documento.getTipo());
        dto.setDescripcion(documento.getDescripcion());
        dto.setCreado(documento.getCreado());
        dto.setActualizado(documento.getActualizado());
        return dto;
    }
}
