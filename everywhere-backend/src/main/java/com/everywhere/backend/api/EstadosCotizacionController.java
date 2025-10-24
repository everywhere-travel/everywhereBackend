package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.EstadoCotizacionRequestDTO;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDTO;
import com.everywhere.backend.security.RequirePermission;
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
    @RequirePermission(module = "COTIZACIONES", permission = "CREATE")
    public ResponseEntity<EstadoCotizacionResponseDTO> create(@RequestBody EstadoCotizacionRequestDTO request) {
        return ResponseEntity.ok(estadoCotizacionService.create(request));
    }

    // Actualizar un estado (id por path, descripcion en body)
    @PutMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<EstadoCotizacionResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody EstadoCotizacionRequestDTO request) {
        return ResponseEntity.ok(estadoCotizacionService.update(id, request));
    }

    // Obtener por id
    @GetMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<EstadoCotizacionResponseDTO> getById(@PathVariable Integer id) {
        return estadoCotizacionService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar todos
    @GetMapping
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<EstadoCotizacionResponseDTO>> getAll() {
        return ResponseEntity.ok(estadoCotizacionService.getAll());
    }

    // Eliminar por id
    @DeleteMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        estadoCotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
