package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.DetalleDocumentoMapper;
import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;
import com.everywhere.backend.model.entity.DetalleDocumento;
import com.everywhere.backend.repository.DetalleDocumentoRepository;
import com.everywhere.backend.service.DetalleDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetalleDocumentoServiceImpl implements DetalleDocumentoService {

    private final DetalleDocumentoRepository detalleDocumentoRepository;
    private final DetalleDocumentoMapper detalleDocumentoMapper;

    @Override
    public List<DetalleDocumentoResponseDto> findByViajeroId(Integer viajeroId) {
        List<DetalleDocumento> detalles = detalleDocumentoRepository.findByViajeroId(viajeroId);
        return detalles.stream()
                .map(detalleDocumentoMapper::toDto)
                .toList();
    }

    @Override
    public DetalleDocumentoResponseDto findById(Integer id) {
        DetalleDocumento detalle = detalleDocumentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleDocumento no encontrado con id: " + id));
        return detalleDocumentoMapper.toDto(detalle);
    }

    @Override
    public DetalleDocumentoResponseDto save(DetalleDocumentoRequestDto dto) {
        DetalleDocumento detalle = detalleDocumentoMapper.toEntity(dto);
        DetalleDocumento guardado = detalleDocumentoRepository.save(detalle);
        return detalleDocumentoMapper.toDto(guardado);
    }

    @Override
    public DetalleDocumentoResponseDto update(Integer id, DetalleDocumentoRequestDto dto) {
        DetalleDocumento existente = detalleDocumentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleDocumento no encontrado con id: " + id));

        // ✅ Actualizamos solo los campos necesarios desde el DTO
        detalleDocumentoMapper.updateEntityFromDto(dto, existente);

        DetalleDocumento actualizado = detalleDocumentoRepository.save(existente);
        return detalleDocumentoMapper.toDto(actualizado);
    }

    @Override
    public void delete(Integer id) {
        if (!detalleDocumentoRepository.existsById(id)) {
            throw new RuntimeException("DetalleDocumento no encontrado con id: " + id);
        }
        detalleDocumentoRepository.deleteById(id);
    }

    @Override
    public List<DetalleDocumentoResponseDto> findAll() {
        return detalleDocumentoRepository.findAll()
                .stream()
                .map(detalleDocumentoMapper::toDto)
                .toList();
    }

    @Override
    public List<DetalleDocumentoResponseDto> findByDocumentoId(Integer documentoId) {
        List<DetalleDocumento> detalles = detalleDocumentoRepository.findByDocumentoId(documentoId);
        return detalles.stream()
                .map(detalleDocumentoMapper::toDto)
                .toList();
    }

    @Override
    public List<DetalleDocumentoResponseDto> findByNumero(String numero) {
        List<DetalleDocumento> detalles = detalleDocumentoRepository.findByNumeroContainingIgnoreCase(numero);
        return detalles.stream()
                .map(detalleDocumentoMapper::toDto)
                .toList();
    }

}
