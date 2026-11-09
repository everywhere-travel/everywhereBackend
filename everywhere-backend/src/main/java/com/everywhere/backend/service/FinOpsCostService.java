package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.finops.FinOpsReportDTO;

public interface FinOpsCostService {

    void registrarConsultaLocalDni();

    void registrarConsultaExternaDni(boolean exitosa);

    void registrarConsultaLocalRuc();

    void registrarConsultaExternaRuc(boolean exitosa);

    void registrarLlamadaGemini(int outputTokens);

    void registrarFallbackGemini();

    FinOpsReportDTO obtenerReporte();
}

