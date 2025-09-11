package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CotizacionRequestDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;
import com.everywhere.backend.security.RequirePermission;
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

    // ----------------- CRUD -----------------

    // Crear sin persona
    @PostMapping
    @RequirePermission(module = "COTIZACIONES", permission = "CREATE")
    public ResponseEntity<CotizacionResponseDto> create(
            @RequestBody CotizacionRequestDto dto
    ) {
        return ResponseEntity.ok(cotizacionService.create(dto, null));
    }

    // Crear con persona (id en la ruta)
    @PostMapping("/persona/{personaId}")
    @RequirePermission(module = "COTIZACIONES", permission = "CREATE")
    public ResponseEntity<CotizacionResponseDto> createWithPersona(
            @PathVariable Integer personaId,
            @RequestBody CotizacionRequestDto dto
    ) {
        return ResponseEntity.ok(cotizacionService.create(dto, personaId));
    }


    // Buscar por ID
    @GetMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<CotizacionResponseDto> findById(@PathVariable Integer id) {
        return cotizacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar todas las cotizaciones
    @GetMapping
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<CotizacionResponseDto>> findAll() {
        return ResponseEntity.ok(cotizacionService.findAll());
    }

    // Actualizar cotización
    @PutMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDto> update(
            @PathVariable Integer id,
            @RequestBody CotizacionRequestDto dto) {
        return ResponseEntity.ok(cotizacionService.update(id, dto));
    }

    // Eliminar cotización
    @DeleteMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------- Asignación de relaciones -----------------

    // Asignar forma de pago
    @PutMapping("/{id}/forma-pago/{formaPagoId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDto> setFormaPago(
            @PathVariable Integer id,
            @PathVariable Integer formaPagoId) {
        return ResponseEntity.ok(cotizacionService.setFormaPagoById(id, formaPagoId));
    }

    // Asignar estado de cotización
    @PutMapping("/{id}/estado/{estadoId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDto> setEstadoCotizacion(
            @PathVariable Integer id,
            @PathVariable Integer estadoId) {
        return ResponseEntity.ok(cotizacionService.setEstadoCotizacionById(id, estadoId));
    }

    // Asignar counter
    @PutMapping("/{id}/counter/{counterId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDto> setCounter(
            @PathVariable Integer id,
            @PathVariable Integer counterId) {
        return ResponseEntity.ok(cotizacionService.setCounterById(id, counterId));
    }

    // Asignar sucursal
    @PutMapping("/{id}/sucursal/{sucursalId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDto> setSucursal(
            @PathVariable Integer id,
            @PathVariable Integer sucursalId) {
        return ResponseEntity.ok(cotizacionService.setSucursalById(id, sucursalId));
    }

    // Asignar persona
    @PutMapping("/{id}/persona/{personaId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDto> setPersonas(
            @PathVariable Integer id,
            @PathVariable Integer personaId) {
        return ResponseEntity.ok(cotizacionService.setPersonasById(id, personaId));
    }
}
