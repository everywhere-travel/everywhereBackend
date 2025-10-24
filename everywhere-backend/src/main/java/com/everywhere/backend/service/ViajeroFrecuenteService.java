package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ViajeroFrecuenteRequestDTO;
import com.everywhere.backend.model.dto.ViajeroFrecuenteResponseDTO;

import java.util.List;

public interface ViajeroFrecuenteService {

    ViajeroFrecuenteResponseDTO crear(Integer viajeroId, ViajeroFrecuenteRequestDTO dto);

    ViajeroFrecuenteResponseDTO buscarPorId(Integer id);

    List<ViajeroFrecuenteResponseDTO> listarPorViajero(Integer viajeroId);

    void eliminar(Integer id);

    ViajeroFrecuenteResponseDTO actualizar(Integer id, ViajeroFrecuenteRequestDTO dto);

    List<ViajeroFrecuenteResponseDTO> buscarPorViajeroId(Integer viajeroId);

}
