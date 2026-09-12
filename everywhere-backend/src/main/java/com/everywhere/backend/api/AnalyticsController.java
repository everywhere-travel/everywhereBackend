package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.analytics.AnalyticsDashboardDTO;
import com.everywhere.backend.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/analytics")
@CrossOrigin("*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Endpoint principal del dashboard de analíticas.
     *
     * @param startDate   Fecha de inicio (por defecto: primer día del mes)
     * @param endDate     Fecha de fin (por defecto: hoy)
     * @param counterId   Filtro opcional por counter/vendedor
     * @param sucursalId  Filtro opcional por sucursal
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsDashboardDTO> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer counterId,
            @RequestParam(required = false) Integer sucursalId) {

        if (startDate == null) startDate = LocalDate.now().withDayOfMonth(1);
        if (endDate   == null) endDate   = LocalDate.now();

        return ResponseEntity.ok(analyticsService.getDashboardData(startDate, endDate, counterId, sucursalId));
    }
}
