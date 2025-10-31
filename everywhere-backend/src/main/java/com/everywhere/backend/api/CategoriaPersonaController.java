package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CategoriaPersonaRequestDTO;
import com.everywhere.backend.model.dto.CategoriaPersonaResponseDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.dto.PersonaNaturalCategoriaDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.CategoriaPersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias-personas")
@RequiredArgsConstructor
public class CategoriaPersonaController {
    
    private final CategoriaPersonaService categoriaPersonaService;

    @GetMapping
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "READ")
    public ResponseEntity<List<CategoriaPersonaResponseDTO>> getAllCategorias() { 
        return ResponseEntity.ok(categoriaPersonaService.findAll());
    }

    @GetMapping("/nombre")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "READ")
    public ResponseEntity<List<CategoriaPersonaResponseDTO>> getCategoriasByNombre(@RequestParam String nombre) { 
        return ResponseEntity.ok(categoriaPersonaService.findByNombre(nombre.trim()));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "READ")
    public ResponseEntity<CategoriaPersonaResponseDTO> getCategoriaById(@PathVariable Integer id) { 
        return ResponseEntity.ok(categoriaPersonaService.findById(id));
    }

    @PostMapping
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "CREATE")
    public ResponseEntity<CategoriaPersonaResponseDTO> createCategoria(@Valid @RequestBody CategoriaPersonaRequestDTO categoriaPersonaRequestDTO) { 
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaPersonaService.save(categoriaPersonaRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "UPDATE")
    public ResponseEntity<CategoriaPersonaResponseDTO> patchCategoria(@PathVariable Integer id, @RequestBody CategoriaPersonaRequestDTO categoriaPersonaRequestDTO) {
        CategoriaPersonaResponseDTO categoriaActualizada = categoriaPersonaService.patch(id, categoriaPersonaRequestDTO);
        return ResponseEntity.ok(categoriaActualizada);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Integer id) {
        categoriaPersonaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/persona-natural/{personaNaturalId}/asignar")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "UPDATE")
    public ResponseEntity<?> asignarCategoria(@PathVariable Integer personaNaturalId, @RequestBody PersonaNaturalCategoriaDTO categoriaDTO) {
        PersonaNaturalResponseDTO personaActualizada = categoriaPersonaService.asignarCategoria(personaNaturalId, categoriaDTO.getCategoriaId());
        return ResponseEntity.ok(personaActualizada);
    }

    @DeleteMapping("/persona-natural/{personaNaturalId}/desasignar")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "UPDATE")
    public ResponseEntity<?> desasignarCategoria(@PathVariable Integer personaNaturalId) {
        PersonaNaturalResponseDTO personaActualizada = categoriaPersonaService.desasignarCategoria(personaNaturalId);
        return ResponseEntity.ok(personaActualizada);
    }

    @GetMapping("/categoria/{categoriaId}")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasPorCategoria(@PathVariable Integer categoriaId) {
        List<PersonaNaturalResponseDTO> personas = categoriaPersonaService.findPersonasPorCategoria(categoriaId);
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/persona-natural/{personaNaturalId}/categoria")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "READ")
    public ResponseEntity<CategoriaPersonaResponseDTO> getCategoriaDePersona(@PathVariable Integer personaNaturalId) {
        CategoriaPersonaResponseDTO categoria = categoriaPersonaService.getCategoriaDePersona(personaNaturalId);
        return ResponseEntity.ok(categoria);
    }
}
