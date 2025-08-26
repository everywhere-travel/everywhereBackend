package com.everywhere.backend.api;


import com.everywhere.backend.model.dto.CounterRequestDto;
import com.everywhere.backend.model.dto.CounterResponseDto;
import com.everywhere.backend.service.CounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/counters")
@RequiredArgsConstructor
public class CounterController {

    private final CounterService counterService;

    @PostMapping
    public ResponseEntity<CounterResponseDto> create(@RequestBody CounterRequestDto request) {
        return ResponseEntity.ok(counterService.create(request));
    }


    @PutMapping
    public ResponseEntity<CounterResponseDto> update(@RequestBody CounterRequestDto request) {
        return ResponseEntity.ok(counterService.update(request));
    }

    @PatchMapping("/activate")
    public ResponseEntity<CounterResponseDto> activate(@RequestBody CounterRequestDto request) {
        return ResponseEntity.ok(counterService.activate(
                request.getCodigo() != null ? request.getCodigo() : request.getNombre()
        ));
    }

    @PatchMapping("/desactivate")
    public ResponseEntity<CounterResponseDto> deactivate(@RequestBody CounterRequestDto request) {
        return ResponseEntity.ok(counterService.deactivate(
                request.getCodigo() != null ? request.getCodigo() : request.getNombre()
        ));
    }

    //Get counter by code
    @GetMapping("/search")
    public ResponseEntity<CounterResponseDto> get(@RequestBody CounterRequestDto request) {
        return counterService.get(
                        request.getCodigo() != null ? request.getCodigo() : request.getNombre()
                ).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Get all counters
    @GetMapping
    public ResponseEntity<List<CounterResponseDto>> getAll() {
        return ResponseEntity.ok(counterService.getAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<CounterResponseDto>> getActivos() {
        return ResponseEntity.ok(counterService.listActive());
    }

    @GetMapping("/inactivos")
    public ResponseEntity<List<CounterResponseDto>> getInactivos() {
        return ResponseEntity.ok(counterService.listInactive());
    }
}