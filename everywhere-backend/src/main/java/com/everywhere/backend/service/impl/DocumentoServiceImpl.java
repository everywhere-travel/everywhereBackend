package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.DocumentoMapper; 
import com.everywhere.backend.model.dto.DocumentoRequestDto;
import com.everywhere.backend.model.dto.DocumentoResponseDto;
import com.everywhere.backend.model.entity.Documento; 
import com.everywhere.backend.repository.DocumentoRepository;
import com.everywhere.backend.service.DocumentoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoMapper documentoMapper;

    @Override
    public List<DocumentoResponseDto> findAll() {
        return documentoRepository.findAll().stream().map(documentoMapper::toResponseDTO).toList();
    }

    @Override
    public DocumentoResponseDto findById(int id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));
        return documentoMapper.toResponseDTO(documento);
    }

    @Override
    public DocumentoResponseDto create(DocumentoRequestDto documentoRequestDto) {
        Documento documento = documentoMapper.toEntity(documentoRequestDto);
        documento = documentoRepository.save(documento);
        return documentoMapper.toResponseDTO(documento);
    }

    @Override
    public DocumentoResponseDto patch(int id, DocumentoRequestDto documentoRequestDto) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));

        documentoMapper.updateEntityFromDto(documentoRequestDto, documento);
        documento = documentoRepository.save(documento);
        return documentoMapper.toResponseDTO(documento);
    }

    @Override
    public void delete(int id) {
        if (!documentoRepository.existsById(id)) throw new RuntimeException("Documento no encontrado con id: " + id);
        documentoRepository.deleteById(id);
    }
}