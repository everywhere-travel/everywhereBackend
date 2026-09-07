package com.everywhere.backend.model.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemographyDTO {
    private String paisNacionalidad;
    private String paisResidencia;
    private Integer cantidadClientes;
}
