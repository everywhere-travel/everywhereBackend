package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.DetalleDocumentoMapper;
import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;
import com.everywhere.backend.model.entity.DetalleDocumento;
import com.everywhere.backend.model.entity.Documento;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.repository.DetalleDocumentoRepository;
import com.everywhere.backend.repository.DocumentoRepository;
import com.everywhere.backend.repository.ViajeroRepository;
import com.everywhere.backend.service.DetalleDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetalleDocumentoServiceImpl implements DetalleDocumentoService {

    private final ViajeroRepository viajeroRepository;
    private final DocumentoRepository documentoRepository;
    private final DetalleDocumentoRepository detalleDocumentoRepository;
    private final DetalleDocumentoMapper detalleDocumentoMapper;

    @Override
    public List<DetalleDocumentoResponseDto> findByViajeroId(Integer viajeroId) {
        List<DetalleDocumento> detalles = detalleDocumentoRepository.findByViajeroId(viajeroId);
        return detalles.stream().map(detalleDocumentoMapper::toResponse).toList();
    }

    @Override
    public DetalleDocumentoResponseDto findById(Integer id) {
        DetalleDocumento detalle = detalleDocumentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleDocumento no encontrado con id: " + id));
        return detalleDocumentoMapper.toResponse(detalle);
    }

    @Override
    public DetalleDocumentoResponseDto save(DetalleDocumentoRequestDto detalleDocumentoRequestDto) {
        DetalleDocumento detalleDocumento = detalleDocumentoMapper.toEntity(detalleDocumentoRequestDto);
        
        if (detalleDocumentoRequestDto.getDocumentoId() != null) {
            Documento documento = documentoRepository.findById(detalleDocumentoRequestDto.getDocumentoId())
                    .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + detalleDocumentoRequestDto.getDocumentoId()));
            detalleDocumento.setDocumento(documento);
        }

        if (detalleDocumentoRequestDto.getViajeroId() != null) {
            Viajero viajero = viajeroRepository.findById(detalleDocumentoRequestDto.getViajeroId())
                    .orElseThrow(() -> new RuntimeException("Viajero no encontrado con id: " + detalleDocumentoRequestDto.getViajeroId()));
            detalleDocumento.setViajero(viajero);
        }

        return detalleDocumentoMapper.toResponse(detalleDocumentoRepository.save(detalleDocumento));
    }

    @Override
    public DetalleDocumentoResponseDto update(Integer id, DetalleDocumentoRequestDto detalleDocumentoRequestDto) {
        DetalleDocumento detalleDocumento = detalleDocumentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleDocumento no encontrado con id: " + id));

        detalleDocumentoMapper.updateEntityFromDto(detalleDocumentoRequestDto, detalleDocumento);

        if (detalleDocumentoRequestDto.getDocumentoId() != null) {
            Documento documento = documentoRepository.findById(detalleDocumentoRequestDto.getDocumentoId())
                    .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + detalleDocumentoRequestDto.getDocumentoId()));
            detalleDocumento.setDocumento(documento);
        }

        if (detalleDocumentoRequestDto.getViajeroId() != null) {
            Viajero viajero = viajeroRepository.findById(detalleDocumentoRequestDto.getViajeroId())
                    .orElseThrow(() -> new RuntimeException("Viajero no encontrado con id: " + detalleDocumentoRequestDto.getViajeroId()));
            detalleDocumento.setViajero(viajero);
        }

        return detalleDocumentoMapper.toResponse(detalleDocumentoRepository.save(detalleDocumento));
    }

    @Override
    public void delete(Integer id) {
        if (!detalleDocumentoRepository.existsById(id)) throw new RuntimeException("DetalleDocumento no encontrado con id: " + id);
        detalleDocumentoRepository.deleteById(id);
    }

    @Override
    public List<DetalleDocumentoResponseDto> findAll() {
        return detalleDocumentoRepository.findAll().stream().map(detalleDocumentoMapper::toResponse).toList();
    }

    @Override
    public List<DetalleDocumentoResponseDto> findByDocumentoId(Integer documentoId) {
        List<DetalleDocumento> detalles = detalleDocumentoRepository.findByDocumentoId(documentoId);
        return detalles.stream().map(detalleDocumentoMapper::toResponse).toList();
    }

    @Override
    public List<DetalleDocumentoResponseDto> findByNumero(String numero) {
        List<DetalleDocumento> detalles = detalleDocumentoRepository.findByNumeroContainingIgnoreCase(numero);
        return detalles.stream().map(detalleDocumentoMapper::toResponse).toList();
    }
}