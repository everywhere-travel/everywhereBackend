package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DetalleCotizacionRequestDto;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;
import com.everywhere.backend.security.RequirePermission;
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
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<DetalleCotizacionResponseDto>> getAll() {
        return ResponseEntity.ok(detalleCotizacionService.findAll());
    }

    //  Obtener por ID
    @GetMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<DetalleCotizacionResponseDto> getById(@PathVariable int id) {
        return detalleCotizacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //  Obtener detalles por cotización
    @GetMapping("/cotizacion/{cotizacionId}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<DetalleCotizacionResponseDto>> getByCotizacionId(@PathVariable int cotizacionId) {
        return ResponseEntity.ok(detalleCotizacionService.findByCotizacionId(cotizacionId));
    }

    //  Crear detalle en una cotización
    @PostMapping("/cotizacion/{cotizacionId}")
    @RequirePermission(module = "COTIZACIONES", permission = "CREATE")
    public ResponseEntity<DetalleCotizacionResponseDto> create(
            @PathVariable int cotizacionId,
            @RequestBody DetalleCotizacionRequestDto dto
    ) {
        return ResponseEntity.ok(detalleCotizacionService.create(dto, cotizacionId));
    }

    //  Actualizar detalle
    @PutMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<DetalleCotizacionResponseDto> update(
            @PathVariable int id,
            @RequestBody DetalleCotizacionRequestDto dto
    ) {
        return ResponseEntity.ok(detalleCotizacionService.update(id, dto));
    }

    //  Eliminar detalle
    @DeleteMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        detalleCotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //  Setear cotización en un detalle
    @PutMapping("/{detalleId}/cotizacion/{cotizacionId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<DetalleCotizacionResponseDto> setCotizacion(
            @PathVariable int detalleId,
            @PathVariable int cotizacionId
    ) {
        return ResponseEntity.ok(detalleCotizacionService.setCotizacion(detalleId, cotizacionId));
    }

    //  Setear producto en un detalle
    @PutMapping("/{detalleId}/producto/{productoId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<DetalleCotizacionResponseDto> setProducto(
            @PathVariable int detalleId,
            @PathVariable int productoId
    ) {
        return ResponseEntity.ok(detalleCotizacionService.setProducto(detalleId, productoId));
    }

    //  Setear proveedor en un detalle
    @PutMapping("/{detalleId}/proveedor/{proveedorId}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<DetalleCotizacionResponseDto> setProveedor(
            @PathVariable int detalleId,
            @PathVariable int proveedorId
    ) {
        return ResponseEntity.ok(detalleCotizacionService.setProveedor(detalleId, proveedorId));
    }

    //  Actualizar solo el campo seleccionado de un detalle
    @PutMapping("/{detalleId}/seleccionado")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<DetalleCotizacionResponseDto> updateSeleccionado(
            @PathVariable("detalleId") int detalleId,
            @RequestParam("seleccionado") Boolean seleccionado
    ) {
        if (detalleId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (seleccionado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(detalleCotizacionService.updateSeleccionado(detalleId, seleccionado));
    }
}
