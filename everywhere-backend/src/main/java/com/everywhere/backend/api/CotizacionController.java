package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CotizacionRequestDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;
import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.CotizacionService;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.io.ByteArrayInputStream;
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cotizacionService.create(cotizacionRequestDto, personaId));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<CotizacionResponseDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(cotizacionService.findById(id));
    }

    @GetMapping("/{id}/con-detalles")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<CotizacionConDetallesResponseDTO> getCotizacionConDetalles(@PathVariable Integer id) {
        return ResponseEntity.ok(cotizacionService.findByIdWithDetalles(id));
    }

    @GetMapping
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<CotizacionResponseDto>> findAll() {
        return ResponseEntity.ok(cotizacionService.findAll());
    }

    @GetMapping("/page")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<Page<CotizacionResponseDto>> getCotizacionesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {
        
        Direction direction = Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        
        return ResponseEntity.ok(cotizacionService.findPage(pageable));
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
    public ResponseEntity<List<CotizacionResponseDto>> getCotizacionesSinLiquidacion() {
        return ResponseEntity.ok(cotizacionService.findCotizacionesSinLiquidacion());
    }

    @GetMapping("/sin-documento-cobranza")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<CotizacionResponseDto>> getCotizacionesSinDocumentoCobranza() {
        return ResponseEntity.ok(cotizacionService.findCotizacionesSinDocumentoCobranza());
    }

    /**
     * Generar documento DOCX de la cotización
     * Acepta GET (sin configuración) o POST (con configuración de vuelos)
     */
    @GetMapping("/{id}/generar-docx")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<Resource> generateDocx(@PathVariable Integer id) {
        ByteArrayInputStream docxStream = cotizacionService.generateDocx(id);

        InputStreamResource resource = new InputStreamResource(docxStream);

        String filename = "Cotizacion_" + id + ".docx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType
                        .parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(resource);
    }

    // Endpoints para gestión de carpetas

    @GetMapping("/carpeta/{carpetaId}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<CotizacionResponseDto>> findByCarpeta(@PathVariable Integer carpetaId) {
        return ResponseEntity.ok(cotizacionService.findByCarpeta(carpetaId));
    }

    @GetMapping("/sin-carpeta")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<CotizacionResponseDto>> findSinCarpeta() {
        return ResponseEntity.ok(cotizacionService.findSinCarpeta());
    }

    @PatchMapping("/{id}/carpeta")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<CotizacionResponseDto> updateCarpeta(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer carpetaId) {
        return ResponseEntity.ok(cotizacionService.updateCarpeta(id, carpetaId));
    }

}