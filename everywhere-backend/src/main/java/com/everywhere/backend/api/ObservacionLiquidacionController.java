package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObeservacionLiquidacionResponseDTO;
import com.everywhere.backend.service.ObservacionLiquidacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/observaciones-liquidacion")
@RequiredArgsConstructor
public class ObservacionLiquidacionController {

    private final ObservacionLiquidacionService observacionLiquidacionService;

    @GetMapping
    public ResponseEntity<List<ObeservacionLiquidacionResponseDTO>> findAll() {
        List<ObeservacionLiquidacionResponseDTO> observaciones = observacionLiquidacionService.findAll();
        return ResponseEntity.ok(observaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObeservacionLiquidacionResponseDTO> findById(@PathVariable Long id) {
        ObeservacionLiquidacionResponseDTO observacion = observacionLiquidacionService.findById(id);
        return ResponseEntity.ok(observacion);
    }

    @PostMapping
    public ResponseEntity<ObeservacionLiquidacionResponseDTO> create(@RequestBody ObservacionLiquidacionRequestDTO requestDTO) {
        ObeservacionLiquidacionResponseDTO observacion = observacionLiquidacionService.save(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(observacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObeservacionLiquidacionResponseDTO> update(@PathVariable Long id, @RequestBody ObservacionLiquidacionRequestDTO requestDTO) {
        ObeservacionLiquidacionResponseDTO observacion = observacionLiquidacionService.update(id, requestDTO);
        return ResponseEntity.ok(observacion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        observacionLiquidacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/liquidacion/{liquidacionId}")
    public ResponseEntity<List<ObeservacionLiquidacionResponseDTO>> findByLiquidacionId(@PathVariable Integer liquidacionId) {
        List<ObeservacionLiquidacionResponseDTO> observaciones = observacionLiquidacionService.findByLiquidacionId(liquidacionId);
        return ResponseEntity.ok(observaciones);
    }
}
