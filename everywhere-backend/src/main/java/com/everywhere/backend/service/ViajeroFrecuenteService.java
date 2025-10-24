package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ViajeroFrecuenteRequestDto;
import com.everywhere.backend.model.dto.ViajeroFrecuenteResponseDto;

import java.util.List;

public interface ViajeroFrecuenteService {

    ViajeroFrecuenteResponseDto crear(Integer viajeroId, ViajeroFrecuenteRequestDto dto);

    ViajeroFrecuenteResponseDto buscarPorId(Integer id);

    List<ViajeroFrecuenteResponseDto> listarPorViajero(Integer viajeroId);

    void eliminar(Integer id);

    ViajeroFrecuenteResponseDto actualizar(Integer id, ViajeroFrecuenteRequestDto dto);

    List<ViajeroFrecuenteResponseDto> buscarPorViajeroId(Integer viajeroId);

}
