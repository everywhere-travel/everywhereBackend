package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.OperadorRequestDTO;
import com.everywhere.backend.model.dto.OperadorResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.OperadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operadores")
@RequiredArgsConstructor
public class OperadorController {

    private final OperadorService operadorService;

    @GetMapping
    @RequirePermission(module = "OPERADOR", permission = "READ")
    public ResponseEntity<List<OperadorResponseDTO>> findAll() {
        List<OperadorResponseDTO> operadores = operadorService.findAll();
        return ResponseEntity.ok(operadores);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "OPERADOR", permission = "READ")
    public ResponseEntity<OperadorResponseDTO> getById(@PathVariable int id) {
        return operadorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequirePermission(module = "OPERADOR", permission = "CREATE")
    public ResponseEntity<OperadorResponseDTO> create(@RequestBody OperadorRequestDTO dto) {
        OperadorResponseDTO response = operadorService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "OPERADOR", permission = "UPDATE")
    public ResponseEntity<OperadorResponseDTO> partialUpdate(
            @PathVariable int id,
            @RequestBody OperadorRequestDTO dto) {
        try {
            OperadorResponseDTO updated = operadorService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "OPERADOR", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (operadorService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        operadorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
