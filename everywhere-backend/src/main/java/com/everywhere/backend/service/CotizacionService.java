package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CotizacionRequestDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;
import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.ByteArrayInputStream;
import java.util.List;

public interface CotizacionService {
    CotizacionResponseDto create(CotizacionRequestDto dto, Integer personaId);
    CotizacionResponseDto findById(Integer id);
    List<CotizacionResponseDto> findAll();
    Page<CotizacionResponseDto> findPage(Pageable pageable);
    CotizacionResponseDto update(Integer id, CotizacionRequestDto dto);
    void delete(Integer id);
    CotizacionConDetallesResponseDTO findByIdWithDetalles(Integer id);
    List<CotizacionResponseDto> findCotizacionesSinLiquidacion();
    List<CotizacionResponseDto> findCotizacionesSinDocumentoCobranza();
    ByteArrayInputStream generateDocx(Integer cotizacionId);
    List<CotizacionResponseDto> findByCarpeta(Integer carpetaId);
    List<CotizacionResponseDto> findSinCarpeta();
    CotizacionResponseDto updateCarpeta(Integer id, Integer carpetaId);
}