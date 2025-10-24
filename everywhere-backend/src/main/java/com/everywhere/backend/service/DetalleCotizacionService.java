package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DetalleCotizacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDTO;

import java.util.List;
import java.util.Optional;

public interface DetalleCotizacionService {

    List<DetalleCotizacionResponseDTO> findAll();

    Optional<DetalleCotizacionResponseDTO> findById(int id);

    List<DetalleCotizacionResponseDTO> findByCotizacionId(int cotizacionId);

    DetalleCotizacionResponseDTO create(DetalleCotizacionRequestDTO dto, int cotizacionId);

    DetalleCotizacionResponseDTO update(int id, DetalleCotizacionRequestDTO dto);

    void delete(int id);

    DetalleCotizacionResponseDTO setCotizacion(int detalleId, int cotizacionId);

    DetalleCotizacionResponseDTO setProducto(int detalleId, int productoId);

    DetalleCotizacionResponseDTO setProveedor(int detalleId, int proveedorId);

    // Método específico para actualizar solo el campo seleccionado
    DetalleCotizacionResponseDTO updateSeleccionado(int detalleId, Boolean seleccionado);
}