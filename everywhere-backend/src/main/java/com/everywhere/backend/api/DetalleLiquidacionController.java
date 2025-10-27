package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DetalleLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.DetalleLiquidacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles-liquidacion")
@RequiredArgsConstructor
public class DetalleLiquidacionController {

    private final DetalleLiquidacionService detalleLiquidacionService;

    @GetMapping
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<List<DetalleLiquidacionResponseDTO>> getAllDetallesLiquidacion() {
        List<DetalleLiquidacionResponseDTO> detalles = detalleLiquidacionService.findAll();
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<DetalleLiquidacionResponseDTO> getDetalleLiquidacionById(@PathVariable Integer id) {
        DetalleLiquidacionResponseDTO detalle = detalleLiquidacionService.findById(id);
        return ResponseEntity.ok(detalle);
    }

    @GetMapping("/liquidacion/{liquidacionId}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<List<DetalleLiquidacionResponseDTO>> getDetallesByLiquidacionId(@PathVariable Integer liquidacionId) {
        List<DetalleLiquidacionResponseDTO> detalles = detalleLiquidacionService.findByLiquidacionId(liquidacionId);
        return ResponseEntity.ok(detalles);
    }

    @PostMapping
    @RequirePermission(module = "LIQUIDACIONES", permission = "CREATE")
    public ResponseEntity<DetalleLiquidacionResponseDTO> createDetalleLiquidacion(@Valid @RequestBody DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        DetalleLiquidacionResponseDTO nuevoDetalle = detalleLiquidacionService.save(detalleLiquidacionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDetalle);
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "UPDATE")
    public ResponseEntity<DetalleLiquidacionResponseDTO> updateDetalleLiquidacion(@PathVariable Integer id, @RequestBody DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        DetalleLiquidacionResponseDTO detalleActualizado = detalleLiquidacionService.update(id, detalleLiquidacionRequestDTO);
        return ResponseEntity.ok(detalleActualizado);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "DELETE")
    public ResponseEntity<Void> deleteDetalleLiquidacion(@PathVariable Integer id) {
        detalleLiquidacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
