package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CarpetaRequestDTO;
import com.everywhere.backend.model.dto.CarpetaResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.CarpetaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carpeta")
public class CarpetaController {

    private final CarpetaService carpetaService;

    @PostMapping
    @RequirePermission(module = "CARPETA", permission = "CREATE")
    public ResponseEntity<CarpetaResponseDTO> create(
            @RequestBody CarpetaRequestDTO carpetaRequestDto,
            @RequestParam(required = false) Integer carpetaPadreId) {

        return ResponseEntity.ok(carpetaService.create(carpetaRequestDto, carpetaPadreId));
    }

    // Buscar por ID
    @GetMapping("/{id}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<CarpetaResponseDTO> findById(@PathVariable Integer id) {
        return carpetaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar todas
    @GetMapping
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDTO>> findAll() {
        return ResponseEntity.ok(carpetaService.findAll());
    }

    // Actualizar
    @PutMapping("/{id}")
    @RequirePermission(module = "CARPETA", permission = "UPDATE")
    public ResponseEntity<CarpetaResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody CarpetaRequestDTO dto) {

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
    public ResponseEntity<List<CarpetaResponseDTO>> findByCarpetaPadre(@PathVariable Integer carpetaPadreId) {
        return ResponseEntity.ok(carpetaService.findByCarpetaPadreId(carpetaPadreId));
    }

    // Listar por nivel
    @GetMapping("/nivel/{nivel}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDTO>> findByNivel(@PathVariable Integer nivel) {
        return ResponseEntity.ok(carpetaService.findByNivel(nivel));
    }

    // Listar por nombre
    @GetMapping("/buscar")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDTO>> findByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(carpetaService.findByNombre(nombre));
    }

    // Listar por año y mes
    @GetMapping("/fecha/{mes}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDTO>> findByMes(@PathVariable int mes) {
        return ResponseEntity.ok(carpetaService.findByMes(mes));
    }

    // Listar por rango de fechas
    @GetMapping("/fecha")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDTO>> findByRango(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin) {
        return ResponseEntity.ok(carpetaService.findByFechaCreacionBetween(inicio, fin));
    }

    // Listar recientes
    @GetMapping("/recientes")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDTO>> findRecent(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(carpetaService.findRecent(limit));
    }

    // Listar raíces (sin padre)
    @GetMapping("/raices")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDTO>> findRaices() {
        return ResponseEntity.ok(carpetaService.findRaices());
    }

    // Encontrar ruta de la carpeta
    @GetMapping("/{id}/camino")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDTO>> findCamino(@PathVariable Integer id) {
        return ResponseEntity.ok(carpetaService.findCamino(id));
    }

    @GetMapping("/hijos/{id}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDTO>> findHijos(@PathVariable Integer id) {
        return ResponseEntity.ok(carpetaService.findByCarpetaPadreId(id));
    }

}