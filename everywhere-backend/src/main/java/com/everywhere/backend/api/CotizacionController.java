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

    // ----------------- CRUD -----------------

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

    // Actualizar cotización
    @PutMapping("/{id}")
    public ResponseEntity<CotizacionResponseDto> update(
            @PathVariable Integer id,
            @RequestBody CotizacionRequestDto dto) {
        return ResponseEntity.ok(cotizacionService.update(id, dto));
    }

    // Eliminar cotización
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------- Asignación de relaciones -----------------

    // Asignar forma de pago
    @PutMapping("/{id}/forma-pago/{formaPagoId}")
    public ResponseEntity<CotizacionResponseDto> setFormaPago(
            @PathVariable Integer id,
            @PathVariable Integer formaPagoId) {
        return ResponseEntity.ok(cotizacionService.setFormaPagoById(id, formaPagoId));
    }

    // Asignar estado de cotización
    @PutMapping("/{id}/estado/{estadoId}")
    public ResponseEntity<CotizacionResponseDto> setEstadoCotizacion(
            @PathVariable Integer id,
            @PathVariable Integer estadoId) {
        return ResponseEntity.ok(cotizacionService.setEstadoCotizacionById(id, estadoId));
    }

    // Asignar counter
    @PutMapping("/{id}/counter/{counterId}")
    public ResponseEntity<CotizacionResponseDto> setCounter(
            @PathVariable Integer id,
            @PathVariable Integer counterId) {
        return ResponseEntity.ok(cotizacionService.setCounterById(id, counterId));
    }

    // Asignar sucursal
    @PutMapping("/{id}/sucursal/{sucursalId}")
    public ResponseEntity<CotizacionResponseDto> setSucursal(
            @PathVariable Integer id,
            @PathVariable Integer sucursalId) {
        return ResponseEntity.ok(cotizacionService.setSucursalById(id, sucursalId));
    }

    // Asignar carpeta
    @PutMapping("/{id}/carpeta/{carpetaId}")
    public ResponseEntity<CotizacionResponseDto> setCarpeta(
            @PathVariable Integer id,
            @PathVariable Integer carpetaId) {
        return ResponseEntity.ok(cotizacionService.setCarpetaById(id, carpetaId));
    }
}
