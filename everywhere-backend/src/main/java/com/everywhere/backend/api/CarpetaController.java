package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CarpetaRequestDto;
import com.everywhere.backend.model.dto.CarpetaResponseDto;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.CarpetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/carpeta")
public class CarpetaController {

    private final CarpetaService carpetaService;

    public CarpetaController(CarpetaService carpetaService) {
        this.carpetaService = carpetaService;
    }

    // Crear carpeta
    @PostMapping
    @RequirePermission(module = "CARPETA", permission = "CREATE")
    public ResponseEntity<CarpetaResponseDto> create(
            @RequestBody CarpetaRequestDto dto,
            @RequestParam(required = false) Integer carpetaPadreId) {

        return ResponseEntity.ok(carpetaService.create(dto, carpetaPadreId));
    }

    // Buscar por ID
    @GetMapping("/{id}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<CarpetaResponseDto> findById(@PathVariable Integer id) {
        return carpetaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar todas
    @GetMapping
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findAll() {
        return ResponseEntity.ok(carpetaService.findAll());
    }

    // Actualizar
    @PutMapping("/{id}")
    @RequirePermission(module = "CARPETA", permission = "UPDATE")
    public ResponseEntity<CarpetaResponseDto> update(
            @PathVariable Integer id,
            @RequestBody CarpetaRequestDto dto) {

        return ResponseEntity.ok(carpetaService.update(id, dto));
    }

    // Eliminar
    @DeleteMapping("/{id}")
    @RequirePermission(module = "CARPETA", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        carpetaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Listar por carpeta padre
    @GetMapping("/padre/{carpetaPadreId}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findByCarpetaPadre(@PathVariable Integer carpetaPadreId) {
        return ResponseEntity.ok(carpetaService.findByCarpetaPadreId(carpetaPadreId));
    }

    // Listar por nivel
    @GetMapping("/nivel/{nivel}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findByNivel(@PathVariable Integer nivel) {
        return ResponseEntity.ok(carpetaService.findByNivel(nivel));
    }

    // Listar por nombre
    @GetMapping("/buscar")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(carpetaService.findByNombre(nombre));
    }

    // Listar por año y mes
    @GetMapping("/fecha/{mes}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findByMes(@PathVariable int mes) {
        return ResponseEntity.ok(carpetaService.findByMes(mes));
    }

    // Listar por rango de fechas
    @GetMapping("/fecha")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findByRango(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin) {
        return ResponseEntity.ok(carpetaService.findByFechaCreacionBetween(inicio, fin));
    }

    // Listar recientes
    @GetMapping("/recientes")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findRecent(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(carpetaService.findRecent(limit));
    }

    // Listar raíces (sin padre)
    @GetMapping("/raices")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findRaices() {
        return ResponseEntity.ok(carpetaService.findRaices());
    }

    // Encontrar ruta de la carpeta
    @GetMapping("/{id}/camino")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findCamino(@PathVariable Integer id) {
        return ResponseEntity.ok(carpetaService.findCamino(id));
    }

    @GetMapping("/hijos/{id}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findHijos(@PathVariable Integer id) {
        return ResponseEntity.ok(carpetaService.findByCarpetaPadreId(id));
    }

}