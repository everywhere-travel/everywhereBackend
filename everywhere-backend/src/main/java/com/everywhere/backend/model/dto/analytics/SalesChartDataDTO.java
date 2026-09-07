package com.everywhere.backend.model.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesChartDataDTO {
    private String fecha; // Can be a string like "2026-09-01" or "01-Sep"
    private BigDecimal monto;
}
