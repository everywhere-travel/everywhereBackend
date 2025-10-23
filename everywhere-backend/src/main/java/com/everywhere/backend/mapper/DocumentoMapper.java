package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DocumentoRequestDTO;
import com.everywhere.backend.model.dto.DocumentoResponseDTO;
import com.everywhere.backend.model.entity.Documento;
import org.springframework.stereotype.Component;

@Component
public class DocumentoMapper {

    // DTO → Entity
    public static Documento toEntity(DocumentoRequestDTO dto) {
        Documento documento = new Documento();
        documento.setTipo(dto.getTipo());
        documento.setDescripcion(dto.getDescripcion());

        return documento;
    }

    // Entity → DTO
    public static DocumentoResponseDTO toDto(Documento documento) {
        DocumentoResponseDTO dto = new DocumentoResponseDTO();
        dto.setId(documento.getId());
        dto.setTipo(documento.getTipo());
        dto.setDescripcion(documento.getDescripcion());
        dto.setEstado(documento.getEstado());
        dto.setCreado(documento.getCreado());
        dto.setActualizado(documento.getActualizado());

        return dto;
    }
}
