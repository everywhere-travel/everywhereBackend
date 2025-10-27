package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObservacionLiquidacionResponseDTO;
import com.everywhere.backend.security.RequirePermission;
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
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<List<ObservacionLiquidacionResponseDTO>> findAll() {
        List<ObservacionLiquidacionResponseDTO> observaciones =
                observacionLiquidacionService.findAll();
        return ResponseEntity.ok(observaciones);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<ObservacionLiquidacionResponseDTO> findById(@PathVariable Long id) {
        ObservacionLiquidacionResponseDTO observacion =
                observacionLiquidacionService.findById(id);
        return ResponseEntity.ok(observacion);
    }

    @PostMapping
    @RequirePermission(module = "LIQUIDACIONES", permission = "CREATE")
    public ResponseEntity<ObservacionLiquidacionResponseDTO> create(
            @RequestBody ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO) {
        ObservacionLiquidacionResponseDTO observacion =
                observacionLiquidacionService.save(observacionLiquidacionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(observacion);
    }


    @PatchMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "UPDATE")
    public ResponseEntity<ObservacionLiquidacionResponseDTO> partialUpdate(
            @PathVariable Long id,
            @RequestBody ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO) {
        ObservacionLiquidacionResponseDTO observacion =
                observacionLiquidacionService.update(id, observacionLiquidacionRequestDTO);
        return ResponseEntity.ok(observacion);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        observacionLiquidacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/liquidacion/{liquidacionId}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<List<ObservacionLiquidacionResponseDTO>> findByLiquidacionId(
            @PathVariable Integer liquidacionId) {
        List<ObservacionLiquidacionResponseDTO> observaciones =
                observacionLiquidacionService.findByLiquidacionId(liquidacionId);
        return ResponseEntity.ok(observaciones);
    }
}
