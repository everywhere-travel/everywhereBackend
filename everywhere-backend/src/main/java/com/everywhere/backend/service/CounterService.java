package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CounterRequestDTO;
import com.everywhere.backend.model.dto.CounterResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CounterService {

    CounterResponseDTO create(CounterRequestDTO dto);

    CounterResponseDTO update(CounterRequestDTO dto);

    CounterResponseDTO activate(String codigo);

    CounterResponseDTO deactivate(String codigo);

    Optional<CounterResponseDTO> get(String codigo);
    Optional<CounterResponseDTO> getByName(String nombre);

    List<CounterResponseDTO> getAll();
    List<CounterResponseDTO> listActive();
    List<CounterResponseDTO> listInactive();

}
