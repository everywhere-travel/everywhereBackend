package com.everywhere.backend.api;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
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
        List<CategoriaPersonaResponseDTO> categorias = categoriaPersonaService.findAll();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/nombre")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "READ")
    public ResponseEntity<List<CategoriaPersonaResponseDTO>> getCategoriasByNombre(@RequestParam String nombre) {
        List<CategoriaPersonaResponseDTO> categorias = categoriaPersonaService.findByNombre(nombre.trim());
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "READ")
    public ResponseEntity<CategoriaPersonaResponseDTO> getCategoriaById(@PathVariable Integer id) {
        CategoriaPersonaResponseDTO categoria = categoriaPersonaService.findById(id);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "CREATE")
    public ResponseEntity<CategoriaPersonaResponseDTO> createCategoria(@Valid @RequestBody CategoriaPersonaRequestDTO categoriaPersonaRequestDTO) {
        CategoriaPersonaResponseDTO nuevaCategoria = categoriaPersonaService.save(categoriaPersonaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
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
        try {
            PersonaNaturalResponseDTO personaActualizada = categoriaPersonaService.asignarCategoria(personaNaturalId, categoriaDTO.getCategoriaId());
            return ResponseEntity.ok(personaActualizada);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al asignar categoría");
        }
    }

    @DeleteMapping("/persona-natural/{personaNaturalId}/desasignar")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "UPDATE")
    public ResponseEntity<?> desasignarCategoria(@PathVariable Integer personaNaturalId) {
        try {
            PersonaNaturalResponseDTO personaActualizada = categoriaPersonaService.desasignarCategoria(personaNaturalId);
            return ResponseEntity.ok(personaActualizada);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desasignar categoría");
        }
    }

    @GetMapping("/categoria/{categoriaId}")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasPorCategoria(@PathVariable Integer categoriaId) {
        try {
            List<PersonaNaturalResponseDTO> personas = categoriaPersonaService.findPersonasPorCategoria(categoriaId);
            return ResponseEntity.ok(personas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/persona-natural/{personaNaturalId}/categoria")
    @RequirePermission(module = "CATEGORIA_PERSONAS", permission = "READ")
    public ResponseEntity<CategoriaPersonaResponseDTO> getCategoriaDePersona(@PathVariable Integer personaNaturalId) {
        try {
            CategoriaPersonaResponseDTO categoria = categoriaPersonaService.getCategoriaDePersona(personaNaturalId);
            if (categoria == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
