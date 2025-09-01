package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.dto.LiquidacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionSimpleDTO;
import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.repository.CarpetaRepository;
import com.everywhere.backend.repository.CotizacionRepository;
import com.everywhere.backend.repository.LiquidacionRepository;
import com.everywhere.backend.service.LiquidacionService;
import com.everywhere.backend.service.DetalleLiquidacionService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.LiquidacionMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiquidacionServiceImpl implements LiquidacionService {

    private final LiquidacionRepository liquidacionRepository;
    private final LiquidacionMapper liquidacionMapper;
    private final DetalleLiquidacionService detalleLiquidacionService;
    private final CotizacionRepository cotizacionRepository;
    private final CarpetaRepository carpetaRepository;

    @Override
    public List<LiquidacionResponseDTO> findAll() {
        return liquidacionRepository.findAllWithRelations().stream()
                .map(liquidacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LiquidacionResponseDTO findById(Integer id) {
        Liquidacion liquidacion = liquidacionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + id));
        return liquidacionMapper.toResponseDTO(liquidacion);
    }

    @Override
    public LiquidacionResponseDTO save(LiquidacionRequestDTO liquidacionRequestDTO) {
        Liquidacion liquidacion = liquidacionMapper.toEntity(liquidacionRequestDTO);
        liquidacion.setCreado(LocalDateTime.now());
        liquidacion = liquidacionRepository.save(liquidacion);

        // Fetch la entidad completa con relaciones después de guardar
        Liquidacion liquidacionCompleta = liquidacionRepository.findByIdWithRelations(liquidacion.getId())
                .orElse(liquidacion);

        return liquidacionMapper.toResponseDTO(liquidacionCompleta);
    }

    @Override
    public LiquidacionResponseDTO update(Integer id, LiquidacionRequestDTO liquidacionRequestDTO) {
        Liquidacion existingLiquidacion = liquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + id));

        liquidacionMapper.updateEntityFromDTO(liquidacionRequestDTO, existingLiquidacion);
        existingLiquidacion = liquidacionRepository.save(existingLiquidacion);

        // Fetch la entidad completa con relaciones después de actualizar
        Liquidacion liquidacionCompleta = liquidacionRepository.findByIdWithRelations(existingLiquidacion.getId())
                .orElse(existingLiquidacion);

        return liquidacionMapper.toResponseDTO(liquidacionCompleta);
    }

    @Override
    public void deleteById(Integer id) {
        if (!liquidacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Liquidación no encontrada con ID: " + id);
        }
        liquidacionRepository.deleteById(id);
    }

    @Override
    public LiquidacionConDetallesResponseDTO findByIdWithDetalles(Integer id) {
        // Obtener la liquidación
        Liquidacion liquidacion = liquidacionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + id));

        // Convertir a DTO base
        LiquidacionResponseDTO liquidacionDTO = liquidacionMapper.toResponseDTO(liquidacion);

        // Obtener los detalles simplificados (sin liquidación repetida)
        List<DetalleLiquidacionResponseDTO> detallesCompletos = detalleLiquidacionService.findByLiquidacionId(id);

        // Convertir a detalles simples (sin liquidación)
        List<DetalleLiquidacionSimpleDTO> detallesSimples = detallesCompletos.stream()
                .map(this::convertirADetalleSimple)
                .collect(Collectors.toList());

        // Crear el DTO con detalles
        LiquidacionConDetallesResponseDTO resultado = new LiquidacionConDetallesResponseDTO();
        resultado.setId(liquidacionDTO.getId());
        resultado.setNumero(liquidacionDTO.getNumero());
        resultado.setFechaCompra(liquidacionDTO.getFechaCompra());
        resultado.setFechaVencimiento(liquidacionDTO.getFechaVencimiento());
        resultado.setDestino(liquidacionDTO.getDestino());
        resultado.setNumeroPasajeros(liquidacionDTO.getNumeroPasajeros());
        resultado.setObservacion(liquidacionDTO.getObservacion());
        resultado.setCreado(liquidacionDTO.getCreado());
        resultado.setActualizado(liquidacionDTO.getActualizado());
        resultado.setProducto(liquidacionDTO.getProducto());
        resultado.setFormaPago(liquidacionDTO.getFormaPago());
        resultado.setDetalles(detallesSimples);

        return resultado;
    }

    private DetalleLiquidacionSimpleDTO convertirADetalleSimple(DetalleLiquidacionResponseDTO detalleCompleto) {
        DetalleLiquidacionSimpleDTO detalleSimple = new DetalleLiquidacionSimpleDTO();
        detalleSimple.setId(detalleCompleto.getId());
        detalleSimple.setTicket(detalleCompleto.getTicket());
        detalleSimple.setCostoTicket(detalleCompleto.getCostoTicket());
        detalleSimple.setCargoServicio(detalleCompleto.getCargoServicio());
        detalleSimple.setValorVenta(detalleCompleto.getValorVenta());
        detalleSimple.setFacturaCompra(detalleCompleto.getFacturaCompra());
        detalleSimple.setBoletaPasajero(detalleCompleto.getBoletaPasajero());
        detalleSimple.setMontoDescuento(detalleCompleto.getMontoDescuento());
        detalleSimple.setPagoPaxUSD(detalleCompleto.getPagoPaxUSD());
        detalleSimple.setPagoPaxPEN(detalleCompleto.getPagoPaxPEN());
        detalleSimple.setCreado(detalleCompleto.getCreado());
        detalleSimple.setActualizado(detalleCompleto.getActualizado());

        // Mapear todas las relaciones (excepto liquidación)
        detalleSimple.setViajero(detalleCompleto.getViajero());
        detalleSimple.setProducto(detalleCompleto.getProducto());
        detalleSimple.setProveedor(detalleCompleto.getProveedor());
        detalleSimple.setOperador(detalleCompleto.getOperador());

        return detalleSimple;
    }

    @Override
    public LiquidacionResponseDTO create(LiquidacionRequestDTO dto, Integer cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new EntityNotFoundException("Cotización no encontrada"));

        Liquidacion liquidacion = liquidacionMapper.toEntity(dto);
        liquidacion.setCotizacion(cotizacion);
        liquidacion.setCreado(LocalDateTime.now());

        Liquidacion saved = liquidacionRepository.save(liquidacion);
        return liquidacionMapper.toResponseDTO(saved);
    }

    @Override
    public LiquidacionResponseDTO setCarpeta(Integer liquidacionId, Integer carpetaId) {
        Liquidacion liquidacion = liquidacionRepository.findById(liquidacionId)
                .orElseThrow(() -> new EntityNotFoundException("Liquidación no encontrada"));

        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new EntityNotFoundException("Carpeta no encontrada"));

        liquidacion.setCarpeta(carpeta);
        liquidacion.setActualizado(LocalDateTime.now());

        Liquidacion updated = liquidacionRepository.save(liquidacion);
        return liquidacionMapper.toResponseDTO(updated);
    }
}
