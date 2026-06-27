package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ReciboResponseDTO;
import com.everywhere.backend.model.dto.ReciboUpdateDTO;
import com.everywhere.backend.security.RequirePermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.everywhere.backend.service.ReciboService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recibos")
public class ReciboController {

    private final ReciboService reciboService;

    /**
     * Crea un nuevo Recibo a partir de un DocumentoCobranza.
     * Un mismo DocumentoCobranza puede tener múltiples Recibos (pagos parciales o totales).
     */
    @PostMapping
    @RequirePermission(module = "RECIBOS", permission = "CREATE")
    public ResponseEntity<ReciboResponseDTO> createRecibo(
            @RequestParam Integer documentoCobranzaId,
            @RequestParam(required = false) Integer personaJuridicaId,
            @RequestParam(required = false) Integer sucursalId,
            @RequestParam(required = false) java.math.BigDecimal montoPago) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reciboService.createRecibo(documentoCobranzaId, personaJuridicaId, sucursalId, montoPago));
    }

    @GetMapping
    @RequirePermission(module = "RECIBOS", permission = "READ")
    public ResponseEntity<List<ReciboResponseDTO>> getAllRecibos() {
        return ResponseEntity.ok(reciboService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "RECIBOS", permission = "READ")
    public ResponseEntity<?> getReciboById(@PathVariable Integer id) {
        return ResponseEntity.ok(reciboService.findById(id));
    }

    /**
     * Retorna todos los recibos vinculados a un DocumentoCobranza.
     * Permite calcular el saldo pagado y pendiente del documento.
     */
    @GetMapping("/documento-cobranza/{documentoCobranzaId}")
    @RequirePermission(module = "RECIBOS", permission = "READ")
    public ResponseEntity<List<ReciboResponseDTO>> getRecibosByDocumentoCobranza(
            @PathVariable Integer documentoCobranzaId) {
        return ResponseEntity.ok(reciboService.findByDocumentoCobranzaId(documentoCobranzaId));
    }

    /**
     * Retorna todos los recibos vinculados a una cotización (puede haber varios).
     * Mantenido por compatibilidad.
     */
    @GetMapping("/cotizacion/{cotizacionId}")
    @RequirePermission(module = "RECIBOS", permission = "READ")
    public ResponseEntity<List<ReciboResponseDTO>> getRecibosByCotizacion(
            @PathVariable Integer cotizacionId) {
        return ResponseEntity.ok(reciboService.findByCotizacionId(cotizacionId));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "RECIBOS", permission = "UPDATE")
    public ResponseEntity<?> updateRecibo(@PathVariable Integer id,
            @Valid @RequestBody ReciboUpdateDTO reciboUpdateDTO) {
        return ResponseEntity.ok(reciboService.patchRecibo(id, reciboUpdateDTO));
    }

    // Endpoints para gestión de carpetas

    @GetMapping("/carpeta/{carpetaId}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<ReciboResponseDTO>> findByCarpeta(@PathVariable Integer carpetaId) {
        return ResponseEntity.ok(reciboService.findByCarpeta(carpetaId));
    }

    @GetMapping("/sin-carpeta")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<ReciboResponseDTO>> findSinCarpeta() {
        return ResponseEntity.ok(reciboService.findSinCarpeta());
    }

    @PatchMapping("/{id}/carpeta")
    @RequirePermission(module = "CARPETA", permission = "UPDATE")
    public ResponseEntity<ReciboResponseDTO> updateCarpeta(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer carpetaId) {
        return ResponseEntity.ok(reciboService.updateCarpeta(id, carpetaId));
    }
}
