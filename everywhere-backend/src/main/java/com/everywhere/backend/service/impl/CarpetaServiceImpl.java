package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CarpetaMapper;
import com.everywhere.backend.model.dto.CarpetaContenidoDTO;
import com.everywhere.backend.model.dto.CarpetaItemDTO;
import com.everywhere.backend.model.dto.CarpetaRequestDto;
import com.everywhere.backend.model.dto.CarpetaResponseDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;
import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.dto.ReciboResponseDTO;
import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.repository.CarpetaRepository;
import com.everywhere.backend.service.CarpetaService;
import com.everywhere.backend.service.CotizacionService;
import com.everywhere.backend.service.DocumentoCobranzaService;
import com.everywhere.backend.service.LiquidacionService;
import com.everywhere.backend.service.ReciboService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CarpetaServiceImpl implements CarpetaService {

    private final CarpetaRepository carpetaRepository;
    private final CarpetaMapper carpetaMapper;
    private final CotizacionService cotizacionService;
    private final LiquidacionService liquidacionService;
    private final ReciboService reciboService;
    private final DocumentoCobranzaService documentoCobranzaService;

    @Override
    @Transactional
    public CarpetaResponseDto create(CarpetaRequestDto carpetaRequestDto, Integer carpetaPadreId) {
        Carpeta carpeta = carpetaMapper.toEntity(carpetaRequestDto);
        
        if (carpetaPadreId != null) { // Primero asignar el nivel correcto
            Carpeta carpetaPadre = carpetaRepository.findById(carpetaPadreId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Carpeta padre no encontrada con ID: " + carpetaPadreId));
            carpeta.setCarpetaPadre(carpetaPadre);
            carpeta.setNivel(carpetaPadre.getNivel() + 1);
        } else {
            carpeta.setNivel(0); // raíz
        }

        if (carpetaRepository.existsByNombreAndNivel(carpeta.getNombre(), carpeta.getNivel()))
            throw new DataIntegrityViolationException("Ya existe una carpeta con el nombre '" + carpeta.getNombre() + "' en el nivel " + carpeta.getNivel());

        return carpetaMapper.toResponse(carpetaRepository.save(carpeta));
    }

    @Override
    public CarpetaResponseDto findById(Integer id) {
        return carpetaRepository.findById(id).map(carpetaMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con ID: " + id));
    }

    @Override
    public List<CarpetaResponseDto> findAll() {
        return mapToResponseList(carpetaRepository.findAll());
    }

    @Override
    @Transactional
    public CarpetaResponseDto update(Integer id, CarpetaRequestDto carpetaRequestDto) {
        Carpeta carpeta = carpetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con ID: " + id));

        carpetaMapper.updateEntityFromRequest(carpetaRequestDto, carpeta);
        return carpetaMapper.toResponse(carpetaRepository.save(carpeta));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!carpetaRepository.existsById(id))
            throw new ResourceNotFoundException("Carpeta no encontrada con ID: " + id);
        carpetaRepository.deleteById(id);
    }

    @Override
    public List<CarpetaResponseDto> findByCarpetaPadreId(Integer carpetaPadreId) {
        if (!carpetaRepository.existsById(carpetaPadreId)) 
            throw new ResourceNotFoundException("Carpeta padre no encontrada con ID: " + carpetaPadreId);
        return mapToResponseList(carpetaRepository.findByCarpetaPadreId(carpetaPadreId));
    }

    @Override
    public List<CarpetaResponseDto> findByNivel(Integer nivel) {
        return mapToResponseList(carpetaRepository.findByNivel(nivel));
    }

    @Override
    public List<CarpetaResponseDto> findByNombre(String nombre) { 
        return mapToResponseList(carpetaRepository.findByNombreContainingIgnoreCase(nombre));
    }

    @Override
    public List<CarpetaResponseDto> findByMes(int mes) {
        int anioActual = LocalDate.now().getYear(); 
        return mapToResponseList(carpetaRepository.findByAnioAndMes(anioActual, mes));
    }

    @Override
    public List<CarpetaResponseDto> findByFechaCreacionBetween(LocalDate inicio, LocalDate fin) {
        LocalDateTime start = inicio.atStartOfDay();
        LocalDateTime end = fin.plusDays(1).atStartOfDay().minusSeconds(1); 
        return mapToResponseList(carpetaRepository.findByCreadoBetweenOrderByCreadoAsc(start, end));
    }

    @Override
    public List<CarpetaResponseDto> findRecent(int limit) {
        List<Carpeta> recientes = carpetaRepository.findAll(PageRequest.of(0, limit, Sort.by("creado").descending())).getContent();
        return mapToResponseList(recientes);
    }

    @Override
    public List<CarpetaResponseDto> findRaices() { 
        return mapToResponseList(carpetaRepository.findByCarpetaPadreIsNull());
    }

    @Override
    public List<CarpetaResponseDto> findCamino(Integer carpetaId) {
        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con ID: " + carpetaId));

        List<Carpeta> camino = new ArrayList<>();

        while (carpeta != null) { // Recorremos hacia arriba hasta la raíz
            camino.add(carpeta);
            carpeta = carpeta.getCarpetaPadre();
        }
        Collections.reverse(camino); // Invertimos para que quede desde la raíz hasta la carpeta actual
        return mapToResponseList(camino);
    }

    @Override
    public List<CarpetaResponseDto> findHijosByPadreId(Integer carpetaPadreId) {
        if (!carpetaRepository.existsById(carpetaPadreId)) 
            throw new ResourceNotFoundException("Carpeta padre no encontrada con ID: " + carpetaPadreId);
        return mapToResponseList(carpetaRepository.findByCarpetaPadreId(carpetaPadreId));
    }

    private List<CarpetaResponseDto> mapToResponseList(List<Carpeta> carpetas) {
        return carpetas.stream().map(carpetaMapper::toResponse).toList();
    }

    @Override
    public CarpetaContenidoDTO getContenido(Integer carpetaId) {
        CarpetaResponseDto carpeta = findById(carpetaId);

        List<CarpetaItemDTO> contenido = new ArrayList<>();
        contenido.addAll(mapCotizaciones(cotizacionService.findByCarpeta(carpetaId)));
        contenido.addAll(mapLiquidaciones(liquidacionService.findByCarpeta(carpetaId)));
        contenido.addAll(mapRecibos(reciboService.findByCarpeta(carpetaId)));
        contenido.addAll(mapDocumentosCobranza(documentoCobranzaService.findByCarpeta(carpetaId)));

        contenido.sort(Comparator.comparing(CarpetaItemDTO::getFecha, Comparator.nullsLast(Comparator.reverseOrder())));

        return CarpetaContenidoDTO.builder()
                .carpeta(carpeta)
                .contenido(contenido)
                .build();
    }

    private List<CarpetaItemDTO> mapCotizaciones(List<CotizacionResponseDto> cotizaciones) {
        return cotizaciones.stream()
                .map(c -> CarpetaItemDTO.builder()
                        .id((long) c.getId())
                        .tipo("cotizacion")
                        .numero(c.getCodigoCotizacion() != null ? c.getCodigoCotizacion() : "COT-" + c.getId())
                        .fecha(c.getFechaEmision() != null ? c.getFechaEmision().toLocalDate() : null)
                        .descripcion(c.getObservacion())
                        .build())
                .toList();
    }

    private List<CarpetaItemDTO> mapLiquidaciones(List<LiquidacionResponseDTO> liquidaciones) {
        return liquidaciones.stream()
                .map(l -> CarpetaItemDTO.builder()
                        .id(l.getId() != null ? l.getId().longValue() : null)
                        .tipo("liquidacion")
                        .numero(l.getNumero() != null ? l.getNumero() : "LIQ-" + l.getId())
                        .fecha(l.getFechaCompra())
                        .descripcion(l.getDestino())
                        .build())
                .toList();
    }

    private List<CarpetaItemDTO> mapRecibos(List<ReciboResponseDTO> recibos) {
        return recibos.stream()
                .map(r -> CarpetaItemDTO.builder()
                        .id(r.getId() != null ? r.getId().longValue() : null)
                        .tipo("recibo")
                        .numero(formatSerieCorrelativo(r.getSerie(), r.getCorrelativo()))
                        .fecha(r.getFechaEmision())
                        .descripcion(r.getObservaciones())
                        .build())
                .toList();
    }

    private List<CarpetaItemDTO> mapDocumentosCobranza(List<DocumentoCobranzaResponseDTO> documentos) {
        return documentos.stream()
                .map(d -> CarpetaItemDTO.builder()
                        .id(d.getId())
                        .tipo("documento-cobranza")
                        .numero(formatSerieCorrelativo(d.getSerie(), d.getCorrelativo()))
                        .fecha(d.getFechaEmision())
                        .descripcion(d.getObservaciones())
                        .build())
                .toList();
    }

    private String formatSerieCorrelativo(String serie, Integer correlativo) {
        if (serie == null || correlativo == null) {
            return null;
        }
        return serie + "-" + String.format("%09d", correlativo);
    }
}