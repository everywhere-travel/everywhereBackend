package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObeservacionLiquidacionResponseDTO;

import java.util.List;

public interface ObservacionLiquidacionService {

    List<ObeservacionLiquidacionResponseDTO> findAll();
    ObeservacionLiquidacionResponseDTO findById(Long id);
    ObeservacionLiquidacionResponseDTO save(ObservacionLiquidacionRequestDTO requestDTO);
    ObeservacionLiquidacionResponseDTO update(Long id, ObservacionLiquidacionRequestDTO requestDTO);
    void deleteById(Long id);
    List<ObeservacionLiquidacionResponseDTO> findByLiquidacionId(Integer liquidacionId);
}
