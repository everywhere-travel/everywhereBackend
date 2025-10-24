package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.DocumentoMapper; 
import com.everywhere.backend.model.dto.DocumentoRequestDto;
import com.everywhere.backend.model.dto.DocumentoResponseDto;
import com.everywhere.backend.model.entity.Documento; 
import com.everywhere.backend.repository.DocumentoRepository;
import com.everywhere.backend.service.DocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentoServiceImpl implements DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Override
    public List<DocumentoResponseDto> findAll() {
        return documentoRepository.findAll()
                .stream()
                .map(DocumentoMapper::toDto)
                .toList();
    }

    @Override
    public DocumentoResponseDto findById(int id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));
        return DocumentoMapper.toDto(documento);
    }

    @Override
    public DocumentoResponseDto create(DocumentoRequestDto dto) {
        Documento documento = DocumentoMapper.toEntity(dto);
        LocalDateTime now = LocalDateTime.now();
        documento.setCreado(now);
        documento.setEstado(Boolean.TRUE);
        Documento saved = documentoRepository.save(documento);
        return DocumentoMapper.toDto(saved);
    }

    @Override
    public DocumentoResponseDto update(int id, DocumentoRequestDto dto) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));

        // actualizar campos
        documento.setTipo(dto.getTipo());
        documento.setDescripcion(dto.getDescripcion());
        LocalDateTime now = LocalDateTime.now();
        documento.setActualizado(now);
        Documento updated = documentoRepository.save(documento);
        return DocumentoMapper.toDto(updated);
    }

    @Override
    public void delete(int id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));

        // Alternar el estado: true -> false, false -> true
        documento.setEstado(!documento.getEstado());
        documento.setActualizado(LocalDateTime.now());

        documentoRepository.save(documento);
    }
}