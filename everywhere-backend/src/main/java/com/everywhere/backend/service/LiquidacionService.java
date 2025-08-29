package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.dto.LiquidacionConDetallesResponseDTO;

import java.util.List;

public interface LiquidacionService {
    List<LiquidacionResponseDTO> findAll();
    LiquidacionResponseDTO findById(Integer id);
    LiquidacionConDetallesResponseDTO findByIdWithDetalles(Integer id);
    LiquidacionResponseDTO save(LiquidacionRequestDTO liquidacionRequestDTO);
    LiquidacionResponseDTO update(Integer id, LiquidacionRequestDTO liquidacionRequestDTO);
    void deleteById(Integer id);
}
