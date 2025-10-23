package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CounterMapper;
import com.everywhere.backend.model.dto.CounterRequestDTO;
import com.everywhere.backend.model.dto.CounterResponseDTO;
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
    public CounterResponseDTO create(CounterRequestDTO dto) {
        Counter entity = CounterMapper.toEntity(dto);
        entity.setCodigo(UUID.randomUUID().toString());
        entity.setFechaCreacion(LocalDateTime.now());
        Counter saved = counterRepository.save(entity);
        return CounterMapper.toResponse(saved);
    }

    @Override
    public CounterResponseDTO update(CounterRequestDTO dto) {
        Counter entity = counterRepository.findByCodigo(dto.getCodigo())
                .orElseThrow(() -> new RuntimeException("Counter no encontrado"));

        Counter updatedEntity = CounterMapper.toEntityForUpdate(dto, entity);
        Counter saved = counterRepository.save(updatedEntity);
        return CounterMapper.toResponse(saved);
    }

    @Override
    public CounterResponseDTO activate(String codigo) {
        Counter entity = counterRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Counter no encontrado"));
        entity.setEstado(true);
        return CounterMapper.toResponse(counterRepository.save(entity));
    }

    @Override
    public CounterResponseDTO deactivate(String codigo) {
        Counter entity = counterRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Counter no encontrado"));
        entity.setEstado(false);
        return CounterMapper.toResponse(counterRepository.save(entity));
    }

    @Override
    public Optional<CounterResponseDTO> get(String codigo) {
        return counterRepository.findByCodigo(codigo)
                .map(CounterMapper::toResponse);
    }

    @Override
    public Optional<CounterResponseDTO> getByName(String nombre) {
        return counterRepository.findByNombreIgnoreCase(nombre)
                .map(CounterMapper::toResponse);
    }

    @Override
    public List<CounterResponseDTO> getAll() {
        return counterRepository.findAll()
                .stream()
                .map(CounterMapper::toResponse)
                .toList();
    }

    @Override
    public List<CounterResponseDTO> listActive() {
        return counterRepository.findAll()
                .stream()
                .filter(Counter::getEstado)
                .map(CounterMapper::toResponse)
                .toList();
    }

    @Override
    public List<CounterResponseDTO> listInactive() {
        return counterRepository.findAll()
                .stream()
                .filter(c -> !c.getEstado())
                .map(CounterMapper::toResponse)
                .toList();
    }
}
