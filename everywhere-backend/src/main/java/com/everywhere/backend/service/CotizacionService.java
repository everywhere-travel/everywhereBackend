package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CotizacionRequestDTO;
import com.everywhere.backend.model.dto.CotizacionResponseDTO;
import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CotizacionService {

    CotizacionResponseDTO create(CotizacionRequestDTO dto, Integer personaId);
    Optional<CotizacionResponseDTO> findById(Integer id);
    List<CotizacionResponseDTO> findAll();
    CotizacionResponseDTO update(Integer id, CotizacionRequestDTO dto);
    void delete(Integer id);

    // Método para obtener cotización con todos sus detalles
    CotizacionConDetallesResponseDTO findByIdWithDetalles(Integer id);

    // Métodos para asignar relaciones por ID
    CotizacionResponseDTO setFormaPagoById(Integer cotizacionId, Integer formaPagoId);
    CotizacionResponseDTO setEstadoCotizacionById(Integer cotizacionId, Integer estadoId);
    CotizacionResponseDTO setCounterById(Integer cotizacionId, Integer counterId);
    CotizacionResponseDTO setSucursalById(Integer cotizacionId, Integer sucursalId);
    CotizacionResponseDTO setCarpetaById(Integer cotizacionId, Integer carpetaId);
    CotizacionResponseDTO setPersonasById(Integer cotizacionId, Integer personasId);

    List<CotizacionResponseDTO> findCotizacionesSinLiquidacion();
}

