package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DocumentoRequestDTO;
import com.everywhere.backend.model.dto.DocumentoResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.DocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @GetMapping
    @RequirePermission(module = "DOCUMENTOS", permission = "CREATE")
    public ResponseEntity<List<DocumentoResponseDTO>> getAll() {
        return ResponseEntity.ok(documentoService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public ResponseEntity<DocumentoResponseDTO> getById(@PathVariable int id) {
        return ResponseEntity.ok(documentoService.findById(id));
    }

    @PostMapping
    @RequirePermission(module = "DOCUMENTOS", permission = "CREATE")
    public ResponseEntity<DocumentoResponseDTO> create(@RequestBody DocumentoRequestDTO dto) {
        return ResponseEntity.ok(documentoService.create(dto));
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS", permission = "UPDATE")
    public ResponseEntity<DocumentoResponseDTO> update(@PathVariable int id, @RequestBody DocumentoRequestDTO dto) {
        return ResponseEntity.ok(documentoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        documentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}