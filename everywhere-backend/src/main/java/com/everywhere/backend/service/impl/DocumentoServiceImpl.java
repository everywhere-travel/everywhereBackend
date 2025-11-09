package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DocumentoMapper; 
import com.everywhere.backend.model.dto.DocumentoRequestDto;
import com.everywhere.backend.model.dto.DocumentoResponseDto;
import com.everywhere.backend.model.entity.Documento; 
import com.everywhere.backend.repository.DocumentoRepository;
import com.everywhere.backend.service.DocumentoService;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "documentos")
public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoMapper documentoMapper;

    @Override
    @Cacheable
    public List<DocumentoResponseDto> findAll() {
        return mapToResponseList(documentoRepository.findAll());
    }

    @Override
    @Cacheable(value = "documentoById", key = "#id")
    public DocumentoResponseDto findById(int id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con id: " + id));
        return documentoMapper.toResponseDTO(documento);
    }

    @Override
    @Transactional
    @CachePut(value = "documentoById", key = "#result.id")
    @CacheEvict(
        value = {
            "documentos"
            // Añadir aquí los cachés de DocumentoCobranza, DetalleDocumento, etc. si existen
            // "documentosCobranza", "detallesDocumento", ...
        }, 
        allEntries = true
    )
    public DocumentoResponseDto create(DocumentoRequestDto documentoRequestDto) {
        Documento documento = documentoMapper.toEntity(documentoRequestDto); 
        return documentoMapper.toResponseDTO(documentoRepository.save(documento));
    }

    @Override
    @Transactional
    @CachePut(value = "documentoById", key = "#id")
    @CacheEvict(
        value = {
            "documentos"
            // Añadir aquí los cachés de DocumentoCobranza, DetalleDocumento, etc. si existen
            // "documentosCobranza", "detallesDocumento", ...
        }, 
        allEntries = true
    )
    public DocumentoResponseDto patch(int id, DocumentoRequestDto documentoRequestDto) {
        if (!documentoRepository.existsById(id))
            throw new ResourceNotFoundException("Documento no encontrado con id: " + id);

        Documento documento = documentoRepository.findById(id).get();
        documentoMapper.updateEntityFromDto(documentoRequestDto, documento); 
        return documentoMapper.toResponseDTO(documentoRepository.save(documento));
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "documentos", "documentoById"
            // Añadir aquí los cachés de DocumentoCobranza, DetalleDocumento, etc. si existen
            // "documentosCobranza", "detallesDocumento", ...
        }, 
        allEntries = true
    )
    public void delete(int id) {
        if (!documentoRepository.existsById(id)) 
            throw new ResourceNotFoundException("Documento no encontrado con id: " + id);
        documentoRepository.deleteById(id);
    }

    private List<DocumentoResponseDto> mapToResponseList(List<Documento> documentos) {
        return documentos.stream().map(documentoMapper::toResponseDTO).toList();
    }
}