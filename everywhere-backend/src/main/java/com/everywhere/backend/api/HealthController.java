package com.everywhere.backend.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Tag(name = "Health & Disponibilidad", description = "Endpoints de disponibilidad, liveness, readiness y telemetría Green Computing")
public class HealthController {

    private final DataSource dataSource;

    @GetMapping
    @Operation(summary = "Chequeo integral de salud del sistema, base de datos y recursos")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "UP");
        status.put("timestamp", Instant.now().toString());

        Map<String, Object> components = new LinkedHashMap<>();
        boolean dbHealthy = false;
        try (Connection conn = dataSource.getConnection()) {
            dbHealthy = conn.isValid(2);
            components.put("database", Map.of(
                    "status", dbHealthy ? "UP" : "DOWN",
                    "driver", conn.getMetaData().getDriverName(),
                    "product", conn.getMetaData().getDatabaseProductName()
            ));
        } catch (Exception e) {
            log.error("[Health] Fallo al verificar conexion con la base de datos: {}", e.getMessage());
            components.put("database", Map.of(
                    "status", "DOWN",
                    "error", e.getMessage() != null ? e.getMessage() : "Error de conectividad"
            ));
        }

        // Green Computing: Telemetria de memoria y utilizacion de CPU de la JVM
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        components.put("greenComputing", Map.of(
                "memoriaUsadaMB", usedMemory / (1024 * 1024),
                "memoriaTotalMB", totalMemory / (1024 * 1024),
                "memoriaMaximaMB", maxMemory / (1024 * 1024),
                "porcentajeUsoMemoria", String.format("%.2f%%", ((double) usedMemory / maxMemory) * 100),
                "nucleosProcesadorDisponibles", runtime.availableProcessors()
        ));

        status.put("components", components);

        if (!dbHealthy) {
            status.put("status", "DEGRADED");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(status);
        }

        return ResponseEntity.ok(status);
    }

    @GetMapping("/liveness")
    @Operation(summary = "Endpoint de liveness probe para orquestadores Docker y VPS")
    public ResponseEntity<Map<String, String>> liveness() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @GetMapping("/readiness")
    @Operation(summary = "Endpoint de readiness probe con validacion de base de datos activa")
    public ResponseEntity<Map<String, Object>> readiness() {
        return checkHealth();
    }
}
