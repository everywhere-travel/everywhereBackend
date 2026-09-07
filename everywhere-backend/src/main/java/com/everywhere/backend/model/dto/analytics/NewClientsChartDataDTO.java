package com.everywhere.backend.model.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewClientsChartDataDTO {
    private String fecha;
    private Integer cantidad;
}
