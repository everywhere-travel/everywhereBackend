package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DetalleCotizacionRequestDto;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;
import com.everywhere.backend.service.DetalleCotizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles-cotizacion")
public class DetalleCotizacionController {

    private final DetalleCotizacionService detalleCotizacionService;

    public DetalleCotizacionController(DetalleCotizacionService detalleCotizacionService) {
        this.detalleCotizacionService = detalleCotizacionService;
    }

    //  Obtener todos
    @GetMapping
    public ResponseEntity<List<DetalleCotizacionResponseDto>> getAll() {
        return ResponseEntity.ok(detalleCotizacionService.findAll());
    }

    //  Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<DetalleCotizacionResponseDto> getById(@PathVariable int id) {
        return detalleCotizacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //  Obtener detalles por cotización
    @GetMapping("/cotizacion/{cotizacionId}")
    public ResponseEntity<List<DetalleCotizacionResponseDto>> getByCotizacionId(@PathVariable int cotizacionId) {
        return ResponseEntity.ok(detalleCotizacionService.findByCotizacionId(cotizacionId));
    }

    //  Crear detalle en una cotización
    @PostMapping("/cotizacion/{cotizacionId}")
    public ResponseEntity<DetalleCotizacionResponseDto> create(
            @PathVariable int cotizacionId,
            @RequestBody DetalleCotizacionRequestDto dto
    ) {
        return ResponseEntity.ok(detalleCotizacionService.create(dto, cotizacionId));
    }

    //  Actualizar detalle
    @PutMapping("/{id}")
    public ResponseEntity<DetalleCotizacionResponseDto> update(
            @PathVariable int id,
            @RequestBody DetalleCotizacionRequestDto dto
    ) {
        return ResponseEntity.ok(detalleCotizacionService.update(id, dto));
    }

    //  Eliminar detalle
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        detalleCotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //  Setear cotización en un detalle
    @PutMapping("/{detalleId}/cotizacion/{cotizacionId}")
    public ResponseEntity<DetalleCotizacionResponseDto> setCotizacion(
            @PathVariable int detalleId,
            @PathVariable int cotizacionId
    ) {
        return ResponseEntity.ok(detalleCotizacionService.setCotizacion(detalleId, cotizacionId));
    }

    //  Setear producto en un detalle
    @PutMapping("/{detalleId}/producto/{productoId}")
    public ResponseEntity<DetalleCotizacionResponseDto> setProducto(
            @PathVariable int detalleId,
            @PathVariable int productoId
    ) {
        return ResponseEntity.ok(detalleCotizacionService.setProducto(detalleId, productoId));
    }

    //  Setear proveedor en un detalle
    @PutMapping("/{detalleId}/proveedor/{proveedorId}")
    public ResponseEntity<DetalleCotizacionResponseDto> setProveedor(
            @PathVariable int detalleId,
            @PathVariable int proveedorId
    ) {
        return ResponseEntity.ok(detalleCotizacionService.setProveedor(detalleId, proveedorId));
    }
}
