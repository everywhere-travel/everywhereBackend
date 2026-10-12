package com.everywhere.backend.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaStatsDTO {
    private long totalEventos;
    private long eventosHoy;
    private long creaciones;
    private long modificaciones;
    private long eliminaciones;
    private long consultas;
}
