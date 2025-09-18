package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CotizacionMapper;
import com.everywhere.backend.model.dto.CotizacionRequestDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;
import com.everywhere.backend.model.entity.*;
import com.everywhere.backend.repository.*;
import com.everywhere.backend.service.CotizacionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final FormaPagoRepository formaPagoRepository;
    private final EstadoCotizacionRepository estadoCotizacionRepository;
    private final CounterRepository counterRepository;
    private final SucursalRepository sucursalRepository;
    private final CarpetaRepository carpetaRepository;
    private final PersonaRepository personasRepository;


    public CotizacionServiceImpl(CotizacionRepository cotizacionRepository,
                                 FormaPagoRepository formaPagoRepository,
                                 EstadoCotizacionRepository estadoCotizacionRepository,
                                 CounterRepository counterRepository,
                                 SucursalRepository sucursalRepository,
                                 CarpetaRepository carpetaRepository, PersonaRepository personasRepository) {
        this.cotizacionRepository = cotizacionRepository;
        this.formaPagoRepository = formaPagoRepository;
        this.estadoCotizacionRepository = estadoCotizacionRepository;
        this.counterRepository = counterRepository;
        this.sucursalRepository = sucursalRepository;
        this.carpetaRepository = carpetaRepository;
        this.personasRepository = personasRepository;
    }

    @Override
    public CotizacionResponseDto create(CotizacionRequestDto dto, Integer personaId) {
        Cotizacion entity = new Cotizacion();

        // Mapear datos desde el DTO
        CotizacionMapper.updateEntityFromRequest(entity, dto);

        // Generar código incremental tipo COT-001, COT-002, etc.
        entity.setCodigoCotizacion(generateCodigoCotizacion());

        // Fechas automáticas
        entity.setFechaEmision(LocalDate.now());
        entity.setActualizado(LocalDateTime.now());

        // Si personaId viene informado, buscar y asignar
        if (personaId != null) {
            Personas persona = personasRepository.findById(personaId)
                    .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada con id " + personaId));
            entity.setPersonas(persona);
        }

        Cotizacion saved = cotizacionRepository.save(entity);
        return CotizacionMapper.toResponse(saved);
    }

    @Override
    public Optional<CotizacionResponseDto> findById(Integer id) {
        return cotizacionRepository.findById(id).map(CotizacionMapper::toResponse);
    }


    @Override
    public List<CotizacionResponseDto> findAll() {
        return cotizacionRepository.findAll()
                .stream()
                .map(CotizacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CotizacionResponseDto update(Integer id, CotizacionRequestDto dto) {
        Cotizacion entity = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        CotizacionMapper.updateEntityFromRequest(entity, dto);
        entity.setActualizado(LocalDateTime.now());

        Cotizacion updated = cotizacionRepository.save(entity);
        return CotizacionMapper.toResponse(updated);
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

    // Métodos para asignar relaciones por ID
    @Override
    public CotizacionResponseDto setFormaPagoById(Integer cotizacionId, Integer formaPagoId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        // Buscar el objeto FormaPago en BD por su id
        FormaPago formaPago = formaPagoRepository.findById(formaPagoId)
                .orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));

        // Asignar el objeto real a la cotización
        cotizacion.setFormaPago(formaPago);

        cotizacion.setActualizado(LocalDateTime.now());
        Cotizacion updated = cotizacionRepository.save(cotizacion);

        return CotizacionMapper.toResponse(updated);
    }

    @Override
    public CotizacionResponseDto setEstadoCotizacionById(Integer cotizacionId, Integer estadoId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        EstadoCotizacion estado = estadoCotizacionRepository.findById(estadoId)
                .orElseThrow(() -> new RuntimeException("Estado de cotización no encontrado"));

        cotizacion.setEstadoCotizacion(estado);
        cotizacion.setActualizado(LocalDateTime.now());

        Cotizacion updated = cotizacionRepository.save(cotizacion);
        return CotizacionMapper.toResponse(updated);
    }

    @Override
    public CotizacionResponseDto setCounterById(Integer cotizacionId, Integer counterId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        Counter counter = counterRepository.findById(counterId)
                .orElseThrow(() -> new RuntimeException("Counter no encontrado"));

        cotizacion.setCounter(counter);
        cotizacion.setActualizado(LocalDateTime.now());

        Cotizacion updated = cotizacionRepository.save(cotizacion);
        return CotizacionMapper.toResponse(updated);
    }



    @Override
    public CotizacionResponseDto setSucursalById(Integer cotizacionId, Integer sucursalId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        cotizacion.setSucursal(sucursal);
        cotizacion.setActualizado(LocalDateTime.now());

        Cotizacion updated = cotizacionRepository.save(cotizacion);
        return CotizacionMapper.toResponse(updated);
    }

    @Override
    public CotizacionResponseDto setCarpetaById(Integer cotizacionId, Integer carpetaId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new RuntimeException("Carpeta no encontrada"));

        cotizacion.setCarpeta(carpeta);
        cotizacion.setActualizado(LocalDateTime.now());

        Cotizacion updated = cotizacionRepository.save(cotizacion);
        return CotizacionMapper.toResponse(updated);
    }

    @Override
    public CotizacionResponseDto setPersonasById(Integer cotizacionId, Integer personaId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        Personas persona = personasRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        cotizacion.setPersonas(persona);
        cotizacion.setActualizado(LocalDateTime.now());

        Cotizacion updated = cotizacionRepository.save(cotizacion);
        return CotizacionMapper.toResponse(updated);
    }



}
