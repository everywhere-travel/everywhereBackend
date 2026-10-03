package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.consulta.DniConsultaResponseDTO;
import com.everywhere.backend.model.dto.consulta.RucConsultaResponseDTO;
import com.everywhere.backend.service.ConsultaPeruService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas Perú", description = "API para consulta y validación de DNI y RUC (Local + ApiPeru.dev)")
public class ConsultaPeruController {

    private final ConsultaPeruService consultaPeruService;

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Consultar DNI", description = "Busca los datos de una persona por su DNI (primero en BD local, luego en ApiPeru RENIEC)")
    public ResponseEntity<DniConsultaResponseDTO> consultarDni(@PathVariable String dni) {
        log.info("--> [Controller] Petición recibida para consultar DNI: {}", dni);
        DniConsultaResponseDTO response = consultaPeruService.consultarDni(dni);
        log.info("<-- [Controller] Fin consulta DNI {}: encontrado={}, origen={}, mensaje='{}'",
                dni, response.isEncontrado(), response.getOrigen(), response.getMensaje());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ruc/{ruc}")
    @Operation(summary = "Consultar RUC", description = "Busca los datos de una empresa/contribuyente por su RUC (primero en BD local, luego en ApiPeru SUNAT)")
    public ResponseEntity<RucConsultaResponseDTO> consultarRuc(@PathVariable String ruc) {
        log.info("--> [Controller] Petición recibida para consultar RUC: {}", ruc);
        RucConsultaResponseDTO response = consultaPeruService.consultarRuc(ruc);
        log.info("<-- [Controller] Fin consulta RUC {}: encontrado={}, origen={}, mensaje='{}'",
                ruc, response.isEncontrado(), response.getOrigen(), response.getMensaje());
        return ResponseEntity.ok(response);
    }
}
