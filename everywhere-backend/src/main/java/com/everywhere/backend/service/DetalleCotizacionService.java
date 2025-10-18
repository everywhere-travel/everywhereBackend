package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DetalleCotizacionRequestDto;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;

import java.util.List;
import java.util.Optional;

public interface DetalleCotizacionService {

    List<DetalleCotizacionResponseDto> findAll();

    Optional<DetalleCotizacionResponseDto> findById(int id);

    List<DetalleCotizacionResponseDto> findByCotizacionId(int cotizacionId);

    DetalleCotizacionResponseDto create(DetalleCotizacionRequestDto dto, int cotizacionId);

    DetalleCotizacionResponseDto update(int id, DetalleCotizacionRequestDto dto);

    void delete(int id);

    DetalleCotizacionResponseDto setCotizacion(int detalleId, int cotizacionId);

    DetalleCotizacionResponseDto setProducto(int detalleId, int productoId);

    DetalleCotizacionResponseDto setProveedor(int detalleId, int proveedorId);

    // Método específico para actualizar solo el campo seleccionado
    DetalleCotizacionResponseDto updateSeleccionado(int detalleId, Boolean seleccionado);
}