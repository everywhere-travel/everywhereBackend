package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CotizacionMapper;
import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.*;
import com.everywhere.backend.repository.*;
import com.everywhere.backend.service.CotizacionService;
import com.everywhere.backend.service.DetalleCotizacionService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final CotizacionMapper cotizacionMapper;
    private final FormaPagoRepository formaPagoRepository;
    private final EstadoCotizacionRepository estadoCotizacionRepository;
    private final CounterRepository counterRepository;
    private final SucursalRepository sucursalRepository;
    private final CarpetaRepository carpetaRepository;
    private final PersonaRepository personasRepository;
    private final DetalleCotizacionService detalleCotizacionService; 

    @Override
    public CotizacionResponseDto create(CotizacionRequestDto cotizacionRequestDto, Integer personaId) {

        Cotizacion cotizacion = cotizacionMapper.toEntity(cotizacionRequestDto);
        cotizacion.setCodigoCotizacion(generateCodigoCotizacion());

        if (personaId == null) throw new ResourceNotFoundException("PersonaId es obligatorio para crear una cotización");
        
        Personas persona = personasRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con id " + personaId));
        cotizacion.setPersonas(persona);

        if (cotizacionRequestDto.getCounterId() != null) {
            Counter counter = counterRepository.findById(cotizacionRequestDto.getCounterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Counter no encontrado con id " + cotizacionRequestDto.getCounterId()));
            cotizacion.setCounter(counter);
        }

        if (cotizacionRequestDto.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(cotizacionRequestDto.getFormaPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con id " + cotizacionRequestDto.getFormaPagoId()));
            cotizacion.setFormaPago(formaPago);
        }

        if (cotizacionRequestDto.getEstadoCotizacionId() != null) {
            EstadoCotizacion estado = estadoCotizacionRepository.findById(cotizacionRequestDto.getEstadoCotizacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estado de cotización no encontrado con id " + cotizacionRequestDto.getEstadoCotizacionId()));
            cotizacion.setEstadoCotizacion(estado);
        }

        if (cotizacionRequestDto.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(cotizacionRequestDto.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id " + cotizacionRequestDto.getSucursalId()));
            cotizacion.setSucursal(sucursal);
        }

        if (cotizacionRequestDto.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(cotizacionRequestDto.getCarpetaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con id " + cotizacionRequestDto.getCarpetaId()));
            cotizacion.setCarpeta(carpeta);
        }

        return cotizacionMapper.toResponse(cotizacionRepository.save(cotizacion));
    }

    @Override
    public Optional<CotizacionResponseDto> findById(Integer id) {
        return cotizacionRepository.findById(id).map(cotizacionMapper::toResponse);
    }

    @Override
    public List<CotizacionResponseDto> findAll() {
        return cotizacionRepository.findAll()
                .stream().map(cotizacionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public CotizacionResponseDto update(Integer id, CotizacionRequestDto cotizacionRequestDto) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        cotizacionMapper.updateEntityFromRequest(cotizacion, cotizacionRequestDto);

        if (cotizacionRequestDto.getCounterId() != null) {
            Counter counter = counterRepository.findById(cotizacionRequestDto.getCounterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Counter no encontrado con id " + cotizacionRequestDto.getCounterId()));
            cotizacion.setCounter(counter);
        }

        if (cotizacionRequestDto.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(cotizacionRequestDto.getFormaPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con id " + cotizacionRequestDto.getFormaPagoId()));
            cotizacion.setFormaPago(formaPago);
        }

        if (cotizacionRequestDto.getEstadoCotizacionId() != null) {
            EstadoCotizacion estado = estadoCotizacionRepository.findById(cotizacionRequestDto.getEstadoCotizacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estado de cotización no encontrado con id " + cotizacionRequestDto.getEstadoCotizacionId()));
            cotizacion.setEstadoCotizacion(estado);
        }

        if (cotizacionRequestDto.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(cotizacionRequestDto.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id " + cotizacionRequestDto.getSucursalId()));
            cotizacion.setSucursal(sucursal);
        }

        if (cotizacionRequestDto.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(cotizacionRequestDto.getCarpetaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con id " + cotizacionRequestDto.getCarpetaId()));
            cotizacion.setCarpeta(carpeta);
        }

        return cotizacionMapper.toResponse(cotizacionRepository.save(cotizacion));
    }

    @Override
    public void delete(Integer id) {
        cotizacionRepository.deleteById(id);
    }

    private String generateCodigoCotizacion() {
        Integer maxId = cotizacionRepository.findMaxId();
        int next = (maxId != null ? maxId + 1 : 1);
        return String.format("COT-%03d", next);
    }

    @Override
    public CotizacionConDetallesResponseDTO findByIdWithDetalles(Integer id) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        CotizacionResponseDto cotizacionDTO = cotizacionMapper.toResponse(cotizacion);
        List<DetalleCotizacionResponseDto> detallesCompletos = detalleCotizacionService.findByCotizacionId(id);
        List<DetalleCotizacionSimpleDTO> detallesSimples = detallesCompletos.stream()
                .map(cotizacionMapper::toDetalleSimple).collect(Collectors.toList());

        return cotizacionMapper.toResponseWithDetalles(cotizacionDTO, detallesSimples);
    }

    @Override
    public List<CotizacionResponseDto> findCotizacionesSinLiquidacion() {
        List<Cotizacion> cotizaciones = cotizacionRepository.findCotizacionesSinLiquidacion();
        return cotizaciones.stream().map(cotizacionMapper::toResponse).collect(Collectors.toList());
    }
}