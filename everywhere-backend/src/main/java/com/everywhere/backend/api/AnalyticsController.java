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

    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsDashboardDTO> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1); // Primer día del mes por defecto
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        return ResponseEntity.ok(analyticsService.getDashboardData(startDate, endDate));
    }
}
