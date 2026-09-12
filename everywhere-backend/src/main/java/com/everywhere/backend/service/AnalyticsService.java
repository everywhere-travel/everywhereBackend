package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.analytics.AnalyticsDashboardDTO;
import java.time.LocalDate;

public interface AnalyticsService {
    AnalyticsDashboardDTO getDashboardData(LocalDate startDate, LocalDate endDate);
    AnalyticsDashboardDTO getDashboardData(LocalDate startDate, LocalDate endDate, Integer counterId, Integer sucursalId);
}
