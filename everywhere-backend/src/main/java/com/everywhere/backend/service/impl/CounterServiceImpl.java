package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CounterMapper;
import com.everywhere.backend.model.dto.CounterRequestDto;
import com.everywhere.backend.model.dto.CounterResponseDto;
import com.everywhere.backend.model.entity.Counter;
import com.everywhere.backend.repository.CounterRepository;
import com.everywhere.backend.service.CounterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CounterServiceImpl implements CounterService {

    private final CounterRepository counterRepository;

    // Constructor (si usas Lombok puedes poner @RequiredArgsConstructor)
    public CounterServiceImpl(CounterRepository counterRepository) {
        this.counterRepository = counterRepository;
    }

    @Override
    public CounterResponseDto create(CounterRequestDto dto) {
        Counter entity = CounterMapper.toEntity(dto);
        entity.setCodigo(UUID.randomUUID().toString());
        entity.setFechaCreacion(LocalDateTime.now());
        Counter saved = counterRepository.save(entity);
        return CounterMapper.toResponse(saved);
    }

    @Override
    public CounterResponseDto update(CounterRequestDto dto) {
        Counter entity = counterRepository.findByCodigo(dto.getCodigo())
                .orElseThrow(() -> new RuntimeException("Counter no encontrado"));

        Counter updatedEntity = CounterMapper.toEntityForUpdate(dto, entity);
        Counter saved = counterRepository.save(updatedEntity);
        return CounterMapper.toResponse(saved);
    }

    @Override
    public CounterResponseDto activate(String codigo) {
        Counter entity = counterRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Counter no encontrado"));
        entity.setEstado(true);
        return CounterMapper.toResponse(counterRepository.save(entity));
    }

    @Override
    public CounterResponseDto deactivate(String codigo) {
        Counter entity = counterRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Counter no encontrado"));
        entity.setEstado(false);
        return CounterMapper.toResponse(counterRepository.save(entity));
    }

    @Override
    public Optional<CounterResponseDto> get(String codigo) {
        return counterRepository.findByCodigo(codigo)
                .map(CounterMapper::toResponse);
    }

    @Override
    public Optional<CounterResponseDto> getByName(String nombre) {
        return counterRepository.findByNombreIgnoreCase(nombre)
                .map(CounterMapper::toResponse);
    }

    @Override
    public List<CounterResponseDto> getAll() {
        return counterRepository.findAll()
                .stream()
                .map(CounterMapper::toResponse)
                .toList();
    }

    @Override
    public List<CounterResponseDto> listActive() {
        return counterRepository.findAll()
                .stream()
                .filter(Counter::getEstado)
                .map(CounterMapper::toResponse)
                .toList();
    }

    @Override
    public List<CounterResponseDto> listInactive() {
        return counterRepository.findAll()
                .stream()
                .filter(c -> !c.getEstado())
                .map(CounterMapper::toResponse)
                .toList();
    }
}
