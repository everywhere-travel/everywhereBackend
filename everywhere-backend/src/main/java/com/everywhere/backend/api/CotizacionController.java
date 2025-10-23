package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CotizacionRequestDTO;
import com.everywhere.backend.model.dto.CotizacionResponseDTO;
import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;
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
    public ResponseEntity<CotizacionResponseDTO> create(
            @RequestBody CotizacionRequestDTO dto
    ) {
        return ResponseEntity.ok(cotizacionService.create(dto, null));
    }

    // Crear con persona (id en la ruta)
    @PostMapping("/persona/{personaId}")
    @RequirePermission(module = "COTIZACIONES", permission = "CREATE")
    public ResponseEntity<CotizacionResponseDTO> createWithPersona(
            @PathVariable Integer personaId,
            @RequestBody CotizacionRequestDTO dto
    ) {
        return ResponseEntity.ok(cotizacionService.create(dto, personaId));
    }


    // Buscar por ID
    @GetMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<CotizacionResponseDTO> findById(@PathVariable Integer id) {
        return cotizacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener cotización con todos sus detalles
    @GetMapping("/{id}/con-detalles")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<CotizacionConDetallesResponseDTO> getCotizacionConDetalles(@PathVariable Integer id) {
        CotizacionConDetallesResponseDTO cotizacionConDetalles = cotizacionService.findByIdWithDetalles(id);
        return ResponseEntity.ok(cotizacionConDetalles);
    }

    // Listar todas las cotizaciones
    @GetMapping
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<CotizacionResponseDTO>> findAll() {
        return ResponseEntity.ok(cotizacionService.findAll());
    }

    // Actualizar cotización
    @PutMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody CotizacionRequestDTO dto) {
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
    public ResponseEntity<CotizacionResponseDTO> setFormaPago(
            @PathVariable Integer id,
            @PathVariable Integer formaPagoId) {
        return ResponseEntity.ok(cotizacionService.setFormaPagoById(id, formaPagoId));
    }

    // Asignar estado de cotización
    @PutMapping("/{id}/estado/{estadoId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDTO> setEstadoCotizacion(
            @PathVariable Integer id,
            @PathVariable Integer estadoId) {
        return ResponseEntity.ok(cotizacionService.setEstadoCotizacionById(id, estadoId));
    }

    // Asignar counter
    @PutMapping("/{id}/counter/{counterId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDTO> setCounter(
            @PathVariable Integer id,
            @PathVariable Integer counterId) {
        return ResponseEntity.ok(cotizacionService.setCounterById(id, counterId));
    }

    // Asignar sucursal
    @PutMapping("/{id}/sucursal/{sucursalId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDTO> setSucursal(
            @PathVariable Integer id,
            @PathVariable Integer sucursalId) {
        return ResponseEntity.ok(cotizacionService.setSucursalById(id, sucursalId));
    }

    // Asignar persona
    @PutMapping("/{id}/persona/{personaId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDTO> setPersonas(
            @PathVariable Integer id,
            @PathVariable Integer personaId) {
        return ResponseEntity.ok(cotizacionService.setPersonasById(id, personaId));
    }

    @GetMapping("/sin-liquidacion")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<CotizacionResponseDTO>> findCotizacionesSinLiquidacion() {
        return ResponseEntity.ok(cotizacionService.findCotizacionesSinLiquidacion());
    }
}
