package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.EstadoCotizacionRequestDto;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDto;
import com.everywhere.backend.service.EstadoCotizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estados-cotizacion")
@RequiredArgsConstructor
public class EstadosCotizacionController {

    private final EstadoCotizacionService estadoCotizacionService;

    // Crear un estado
    @PostMapping
    public ResponseEntity<EstadoCotizacionResponseDto> create(@RequestBody EstadoCotizacionRequestDto request) {
        return ResponseEntity.ok(estadoCotizacionService.create(request));
    }

    // Actualizar un estado (id por path, descripcion en body)
    @PutMapping("/{id}")
    public ResponseEntity<EstadoCotizacionResponseDto> update(
            @PathVariable Integer id,
            @RequestBody EstadoCotizacionRequestDto request) {
        return ResponseEntity.ok(estadoCotizacionService.update(id, request));
    }

    // Obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<EstadoCotizacionResponseDto> getById(@PathVariable Integer id) {
        return estadoCotizacionService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<List<EstadoCotizacionResponseDto>> getAll() {
        return ResponseEntity.ok(estadoCotizacionService.getAll());
    }

    // Eliminar por id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        estadoCotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
