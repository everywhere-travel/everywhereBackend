package com.everywhere.backend.api;


import com.everywhere.backend.model.dto.CounterRequestDTO;
import com.everywhere.backend.model.dto.CounterResponseDTO;
import com.everywhere.backend.security.RequirePermission;
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
    @RequirePermission(module = "COUNTERS", permission = "CREATE")
    public ResponseEntity<CounterResponseDTO> create(@RequestBody CounterRequestDTO request) {
        return ResponseEntity.ok(counterService.create(request));
    }


    @PutMapping
    @RequirePermission(module = "COUNTERS", permission = "UPDATE")
    public ResponseEntity<CounterResponseDTO> update(@RequestBody CounterRequestDTO request) {
        return ResponseEntity.ok(counterService.update(request));
    }

    @PatchMapping("/activate")
    @RequirePermission(module = "COUNTERS", permission = "UPDATE")
    public ResponseEntity<CounterResponseDTO> activate(@RequestBody CounterRequestDTO request) {
        return ResponseEntity.ok(counterService.activate(
                request.getCodigo() != null ? request.getCodigo() : request.getNombre()
        ));
    }

    @PatchMapping("/desactivate")
    @RequirePermission(module = "COUNTERS", permission = "UPDATE")
    public ResponseEntity<CounterResponseDTO> deactivate(@RequestBody CounterRequestDTO request) {
        return ResponseEntity.ok(counterService.deactivate(
                request.getCodigo() != null ? request.getCodigo() : request.getNombre()
        ));
    }

    //Get counter by code
    @GetMapping("/search")
    @RequirePermission(module = "COUNTERS", permission = "READ")
    public ResponseEntity<CounterResponseDTO> get(@RequestBody CounterRequestDTO request) {
        return counterService.get(
                        request.getCodigo() != null ? request.getCodigo() : request.getNombre()
                ).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Get all counters
    @GetMapping
    @RequirePermission(module = "COUNTERS", permission = "READ")
    public ResponseEntity<List<CounterResponseDTO>> getAll() {
        return ResponseEntity.ok(counterService.getAll());
    }

    @GetMapping("/activos")
    @RequirePermission(module = "COUNTERS", permission = "READ")
    public ResponseEntity<List<CounterResponseDTO>> getActivos() {
        return ResponseEntity.ok(counterService.listActive());
    }

    @GetMapping("/inactivos")
    @RequirePermission(module = "COUNTERS", permission = "READ")
    public ResponseEntity<List<CounterResponseDTO>> getInactivos() {
        return ResponseEntity.ok(counterService.listInactive());
    }
}