package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DetalleLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;

import java.util.List;

public interface DetalleLiquidacionService {

    List<DetalleLiquidacionResponseDTO> findAll();

    DetalleLiquidacionResponseDTO findById(Integer id);

    List<DetalleLiquidacionResponseDTO> findByLiquidacionId(Integer liquidacionId);

    DetalleLiquidacionResponseDTO save(DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO);

    DetalleLiquidacionResponseDTO update(Integer id, DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO);

    void deleteById(Integer id);
}
