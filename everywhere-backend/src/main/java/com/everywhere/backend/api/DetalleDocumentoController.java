package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DetalleDocumentoResponseDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoRequestDTO;
import com.everywhere.backend.service.DetalleDocumentoService;
import com.everywhere.backend.security.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalle-documento")
@RequiredArgsConstructor
public class DetalleDocumentoController {

    private final DetalleDocumentoService detalleDocumentoService;

    @GetMapping
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public List<DetalleDocumentoResponseDTO> findAll() {
        return detalleDocumentoService.findAll();
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public DetalleDocumentoResponseDTO findById(@PathVariable Integer id) {
        return detalleDocumentoService.findById(id);
    }

    @GetMapping("/viajero/{viajeroId}")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public List<DetalleDocumentoResponseDTO> findByViajero(@PathVariable Integer viajeroId) {
        return detalleDocumentoService.findByViajeroId(viajeroId);
    }

    @GetMapping("/documento/{documentoId}")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public List<DetalleDocumentoResponseDTO> findByDocumentoId(@PathVariable Integer documentoId) {
        return detalleDocumentoService.findByDocumentoId(documentoId);
    }

    @GetMapping("/numero/{numero}")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public List<DetalleDocumentoResponseDTO> findByNumero(@PathVariable String numero) {
        return detalleDocumentoService.findByNumero(numero);
    }

    @PostMapping
    @RequirePermission(module = "DOCUMENTOS", permission = "CREATE")
    public DetalleDocumentoResponseDTO save(@RequestBody DetalleDocumentoRequestDTO dto) {
        return detalleDocumentoService.save(dto);
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS", permission = "UPDATE")
    public DetalleDocumentoResponseDTO update(
            @PathVariable Integer id,
            @RequestBody DetalleDocumentoRequestDTO dto
    ) {
        return detalleDocumentoService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "DELETE")
    public void delete(@PathVariable Integer id) {
        detalleDocumentoService.delete(id);
    }
}
