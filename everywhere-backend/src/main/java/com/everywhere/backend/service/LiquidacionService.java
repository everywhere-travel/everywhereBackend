package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.dto.LiquidacionConDetallesResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface LiquidacionService {
    List<LiquidacionResponseDTO> findAll();
    Page<LiquidacionResponseDTO> findPage(Pageable pageable);
    LiquidacionResponseDTO findById(Integer id);
    LiquidacionConDetallesResponseDTO findByIdWithDetalles(Integer id);
    LiquidacionResponseDTO update(Integer id, LiquidacionRequestDTO liquidacionRequestDTO);
    void deleteById(Integer id);
    LiquidacionResponseDTO create(LiquidacionRequestDTO liquidacionRequestDTO, Integer cotizacionId);
    List<LiquidacionResponseDTO> findByCarpeta(Integer carpetaId);
    List<LiquidacionResponseDTO> findSinCarpeta();
    LiquidacionResponseDTO updateCarpeta(Integer id, Integer carpetaId);
    ByteArrayInputStream generateExcel(Integer liquidacionId);
}