package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.consulta.DniConsultaResponseDTO;
import com.everywhere.backend.model.dto.consulta.RucConsultaResponseDTO;

public interface ConsultaPeruService {
    DniConsultaResponseDTO consultarDni(String dni);
    RucConsultaResponseDTO consultarRuc(String ruc);
}
