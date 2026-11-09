package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.finops.FinOpsReportDTO;
import com.everywhere.backend.service.FinOpsCostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telemetria/finops")
@RequiredArgsConstructor
@Tag(name = "FinOps y Gobernanza OPEX", description = "Endpoints de telemetría de consumo de APIs cloud y ahorro de costos operativos")
public class FinOpsTelemetryController {

    private final FinOpsCostService finOpsCostService;

    @GetMapping
    @Operation(summary = "Obtener métricas de costos OPEX y ahorro por caché local")
    public ResponseEntity<FinOpsReportDTO> obtenerReporteFinOps() {
        return ResponseEntity.ok(finOpsCostService.obtenerReporte());
    }
}

