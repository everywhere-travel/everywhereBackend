package com.everywhere.backend.model.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDashboardDTO {
    private KpiSummaryDTO kpis;
    private List<TopVendorDTO> topVendors;
    private List<TopProductDTO> topProducts;
    private List<SalesChartDataDTO> salesChart;
    
    private List<TopClientDTO> topClients;
    private List<DemographyDTO> clientDemographics;
    private List<TopDestinationDTO> topDestinations;
    private List<NewClientsChartDataDTO> newClientsChart;

    // Nuevos: Análisis de Conversión
    private QuoteFunnelDTO quoteFunnel;
    private List<ConversionByVendorDTO> conversionByVendor;
}
