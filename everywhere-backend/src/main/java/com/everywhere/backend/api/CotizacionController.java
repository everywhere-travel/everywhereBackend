package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CotizacionRequestDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;
import com.everywhere.backend.service.CotizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    public CotizacionController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    // Crear nueva cotización
    @PostMapping
    public ResponseEntity<CotizacionResponseDto> create(@RequestBody CotizacionRequestDto dto) {
        return ResponseEntity.ok(cotizacionService.create(dto));
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<CotizacionResponseDto> findById(@PathVariable Integer id) {
        return cotizacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar todas las cotizaciones
    @GetMapping
    public ResponseEntity<List<CotizacionResponseDto>> findAll() {
        return ResponseEntity.ok(cotizacionService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CotizacionResponseDto> update(
            @PathVariable Integer id,
            @RequestBody CotizacionRequestDto dto) {
        System.out.println("DEBUG: ID recibido en update = " + id);
        return ResponseEntity.ok(cotizacionService.update(id, dto));
    }


    // Eliminar cotización
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
