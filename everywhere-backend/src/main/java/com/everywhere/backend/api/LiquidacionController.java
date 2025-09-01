package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.dto.LiquidacionConDetallesResponseDTO;
import com.everywhere.backend.service.LiquidacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/liquidaciones")
@RequiredArgsConstructor
public class LiquidacionController {

    private final LiquidacionService liquidacionService;

    @GetMapping
    public ResponseEntity<List<LiquidacionResponseDTO>> getAllLiquidaciones() {
        List<LiquidacionResponseDTO> liquidaciones = liquidacionService.findAll();
        return ResponseEntity.ok(liquidaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiquidacionResponseDTO> getLiquidacionById(@PathVariable Integer id) {
        LiquidacionResponseDTO liquidacion = liquidacionService.findById(id);
        return ResponseEntity.ok(liquidacion);
    }

    @PostMapping
    public ResponseEntity<LiquidacionResponseDTO> createLiquidacion(@Valid @RequestBody LiquidacionRequestDTO liquidacionRequestDTO) {
        LiquidacionResponseDTO nuevaLiquidacion = liquidacionService.save(liquidacionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaLiquidacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LiquidacionResponseDTO> updateLiquidacion(@PathVariable Integer id, @Valid @RequestBody LiquidacionRequestDTO liquidacionRequestDTO) {
        LiquidacionResponseDTO liquidacionActualizada = liquidacionService.update(id, liquidacionRequestDTO);
        return ResponseEntity.ok(liquidacionActualizada);
    }

    @GetMapping("/{id}/con-detalles")
    public ResponseEntity<LiquidacionConDetallesResponseDTO> getLiquidacionConDetalles(@PathVariable Integer id) {
        LiquidacionConDetallesResponseDTO liquidacionConDetalles = liquidacionService.findByIdWithDetalles(id);
        return ResponseEntity.ok(liquidacionConDetalles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLiquidacion(@PathVariable Integer id) {
        liquidacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cotizacion/{cotizacionId}")
    public ResponseEntity<LiquidacionResponseDTO> createLiquidacionConCotizacion(
            @PathVariable Integer cotizacionId,
            @Valid @RequestBody LiquidacionRequestDTO liquidacionRequestDTO
    ) {
        LiquidacionResponseDTO nuevaLiquidacion = liquidacionService.create(liquidacionRequestDTO, cotizacionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaLiquidacion);
    }

    @PutMapping("/{liquidacionId}/carpeta/{carpetaId}")
    public ResponseEntity<LiquidacionResponseDTO> setCarpeta(
            @PathVariable Integer liquidacionId,
            @PathVariable Integer carpetaId
    ) {
        LiquidacionResponseDTO liquidacionActualizada = liquidacionService.setCarpeta(liquidacionId, carpetaId);
        return ResponseEntity.ok(liquidacionActualizada);
    }
}
