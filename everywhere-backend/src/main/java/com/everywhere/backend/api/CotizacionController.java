package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CotizacionRequestDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;
import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.CotizacionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    @PostMapping("/persona/{personaId}")
    @RequirePermission(module = "COTIZACIONES", permission = "CREATE")
    public ResponseEntity<CotizacionResponseDto> createWithPersona(
            @PathVariable Integer personaId, @RequestBody CotizacionRequestDto cotizacionRequestDto) {
        return ResponseEntity.ok(cotizacionService.create(cotizacionRequestDto, personaId));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<CotizacionResponseDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(cotizacionService.findById(id));
    }

    @GetMapping("/{id}/con-detalles")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<CotizacionConDetallesResponseDTO> getCotizacionConDetalles(@PathVariable Integer id) {
        CotizacionConDetallesResponseDTO cotizacionConDetalles = cotizacionService.findByIdWithDetalles(id);
        return ResponseEntity.ok(cotizacionConDetalles);
    }

    @GetMapping
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<CotizacionResponseDto>> findAll() {
        return ResponseEntity.ok(cotizacionService.findAll());
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDto> update(
            @PathVariable Integer id, @RequestBody CotizacionRequestDto cotizacionRequestDto) {
        return ResponseEntity.ok(cotizacionService.update(id, cotizacionRequestDto));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sin-liquidacion")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<CotizacionResponseDto>> findCotizacionesSinLiquidacion() {
        return ResponseEntity.ok(cotizacionService.findCotizacionesSinLiquidacion());
    }
}