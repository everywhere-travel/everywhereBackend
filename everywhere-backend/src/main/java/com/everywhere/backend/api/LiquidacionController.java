package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.dto.LiquidacionConDetallesResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.LiquidacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/liquidaciones")
@RequiredArgsConstructor
public class LiquidacionController {

    private final LiquidacionService liquidacionService;

    @GetMapping
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<List<LiquidacionResponseDTO>> getAllLiquidaciones() { 
        return ResponseEntity.ok(liquidacionService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<LiquidacionResponseDTO> getLiquidacionById(@PathVariable Integer id) { 
        return ResponseEntity.ok(liquidacionService.findById(id));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "UPDATE")
    public ResponseEntity<LiquidacionResponseDTO> updateLiquidacion(
            @PathVariable Integer id, @RequestBody LiquidacionRequestDTO liquidacionRequestDTO) {
        return ResponseEntity.ok(liquidacionService.update(id, liquidacionRequestDTO));
    }

    @GetMapping("/{id}/con-detalles")
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<LiquidacionConDetallesResponseDTO> getLiquidacionConDetalles(@PathVariable Integer id) { 
        return ResponseEntity.ok(liquidacionService.findByIdWithDetalles(id));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "DELETE")
    public ResponseEntity<Void> deleteLiquidacion(@PathVariable Integer id) {
        liquidacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cotizacion/{cotizacionId}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "CREATE")
    public ResponseEntity<LiquidacionResponseDTO> createLiquidacionConCotizacion(
            @PathVariable Integer cotizacionId, @Valid @RequestBody LiquidacionRequestDTO liquidacionRequestDTO) { 
        return ResponseEntity.ok(liquidacionService.create(liquidacionRequestDTO, cotizacionId));
    }
}
