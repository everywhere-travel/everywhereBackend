package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DetalleDocumentoMapper;
import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;
import com.everywhere.backend.model.entity.DetalleDocumento;
import com.everywhere.backend.model.entity.Documento;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.repository.DetalleDocumentoRepository;
import com.everywhere.backend.repository.DocumentoRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.service.DetalleDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetalleDocumentoServiceImpl implements DetalleDocumentoService {

    private final PersonaNaturalRepository personaNaturalRepository;
    private final DocumentoRepository documentoRepository;
    private final DetalleDocumentoRepository detalleDocumentoRepository;
    private final DetalleDocumentoMapper detalleDocumentoMapper;

    @Override
    public DetalleDocumentoResponseDto findById(Integer id) {
        DetalleDocumento detalle = detalleDocumentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetalleDocumento no encontrado con id: " + id));
        return detalleDocumentoMapper.toResponse(detalle);
    }

    @Override
    @Transactional
    public DetalleDocumentoResponseDto save(DetalleDocumentoRequestDto detalleDocumentoRequestDto) {
        DetalleDocumento detalleDocumento = detalleDocumentoMapper.toEntity(detalleDocumentoRequestDto);
        
        if (detalleDocumentoRequestDto.getDocumentoId() != null) {
            Documento documento = documentoRepository.findById(detalleDocumentoRequestDto.getDocumentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con id: " + detalleDocumentoRequestDto.getDocumentoId()));
            detalleDocumento.setDocumento(documento);
        }

        if (detalleDocumentoRequestDto.getPersonaNaturalId() != null) {
            PersonaNatural personaNatural = personaNaturalRepository.findById(detalleDocumentoRequestDto.getPersonaNaturalId())
                    .orElseThrow(() -> new ResourceNotFoundException("PersonaNatural no encontrado con id: " + detalleDocumentoRequestDto.getPersonaNaturalId()));
            detalleDocumento.setPersonaNatural(personaNatural);
        }

        return detalleDocumentoMapper.toResponse(detalleDocumentoRepository.save(detalleDocumento));
    }

    @Override
    @Transactional
    public DetalleDocumentoResponseDto update(Integer id, DetalleDocumentoRequestDto detalleDocumentoRequestDto) {
        DetalleDocumento detalleDocumento = detalleDocumentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetalleDocumento no encontrado con id: " + id));

        detalleDocumentoMapper.updateEntityFromDto(detalleDocumentoRequestDto, detalleDocumento);

        if (detalleDocumentoRequestDto.getDocumentoId() != null) {
            Documento documento = documentoRepository.findById(detalleDocumentoRequestDto.getDocumentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con id: " + detalleDocumentoRequestDto.getDocumentoId()));
            detalleDocumento.setDocumento(documento);
        }

        if (detalleDocumentoRequestDto.getPersonaNaturalId() != null) {
            PersonaNatural personaNatural = personaNaturalRepository.findById(detalleDocumentoRequestDto.getPersonaNaturalId())
                    .orElseThrow(() -> new ResourceNotFoundException("PersonaNatural no encontrado con id: " + detalleDocumentoRequestDto.getPersonaNaturalId()));
            detalleDocumento.setPersonaNatural(personaNatural);
        }

        return detalleDocumentoMapper.toResponse(detalleDocumentoRepository.save(detalleDocumento));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!detalleDocumentoRepository.existsById(id)) 
            throw new ResourceNotFoundException("DetalleDocumento no encontrado con id: " + id);
        detalleDocumentoRepository.deleteById(id);
    }

    @Override
    public List<DetalleDocumentoResponseDto> findAll() {
        return detalleDocumentoRepository.findAll().stream().map(detalleDocumentoMapper::toResponse).toList();
    }

    @Override
    public List<DetalleDocumentoResponseDto> findByDocumentoId(Integer documentoId) {
        if (!documentoRepository.existsById(documentoId)) 
            throw new ResourceNotFoundException("Documento no encontrado con id: " + documentoId);
        List<DetalleDocumento> detalles = detalleDocumentoRepository.findByDocumentoId(documentoId);
        return detalles.stream().map(detalleDocumentoMapper::toResponse).toList();
    }

    @Override
    public List<DetalleDocumentoResponseDto> findByNumero(String numero) {
        List<DetalleDocumento> detalles = detalleDocumentoRepository.findByNumeroContainingIgnoreCase(numero);
        return detalles.stream().map(detalleDocumentoMapper::toResponse).toList();
    }
}