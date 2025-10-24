package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CounterRequestDto;
import com.everywhere.backend.model.dto.CounterResponseDto;

import java.util.List;
import java.util.Optional;

public interface CounterService {

    CounterResponseDto create(CounterRequestDto dto);

    CounterResponseDto update(CounterRequestDto dto);

    CounterResponseDto activate(String codigo);

    CounterResponseDto deactivate(String codigo);

    Optional<CounterResponseDto> get(String codigo);
    Optional<CounterResponseDto> getByName(String nombre);

    List<CounterResponseDto> getAll();
    List<CounterResponseDto> listActive();
    List<CounterResponseDto> listInactive();

}
