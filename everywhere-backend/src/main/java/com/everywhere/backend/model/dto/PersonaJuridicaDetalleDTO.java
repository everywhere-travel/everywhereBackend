package com.everywhere.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaJuridicaDetalleDTO {
    private PersonaJuridicaResponseDTO personaJuridica;
    private List<PersonaNaturalResponseDTO> clientesAsociados;
}
