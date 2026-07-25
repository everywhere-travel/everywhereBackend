package com.everywhere.backend.utils;

import com.everywhere.backend.model.dto.*;

import java.time.LocalDateTime;
import java.util.Collections;

public class PersonaTestData {

    public static PersonaRequestDTO getValidRequest() {
        PersonaRequestDTO request = new PersonaRequestDTO();
        request.setDireccion("Av. Las Flores 123");
        request.setObservacion("Cliente VIP");
        return request;
    }

    public static PersonaResponseDTO getValidResponse() {
        PersonaResponseDTO response = new PersonaResponseDTO();
        response.setId(1);
        response.setDireccion("Av. Las Flores 123");
        response.setObservacion("Cliente VIP");
        response.setCreado(LocalDateTime.now());
        response.setActualizado(LocalDateTime.now());
        response.setTelefonos(Collections.emptyList());
        response.setCorreos(Collections.emptyList());
        return response;
    }

    public static PersonaDisplayDto getValidDisplay() {
        PersonaDisplayDto display = new PersonaDisplayDto();
        display.setId(1);
        display.setTipo("NATURAL");
        display.setIdentificador("77778888");
        display.setNombre("Juan Perez");
        return display;
    }

    public static PersonaNaturalRequestDTO getValidNaturalRequest() {
        PersonaNaturalRequestDTO request = new PersonaNaturalRequestDTO();
        request.setDocumento("77778888");
        request.setNombres("Juan");
        request.setApellidosPaterno("Perez");
        request.setApellidosMaterno("Gomez");
        request.setSexo("M");
        request.setPersona(getValidRequest());
        return request;
    }

    public static PersonaNaturalResponseDTO getValidNaturalResponse() {
        PersonaNaturalResponseDTO response = new PersonaNaturalResponseDTO();
        response.setId(1);
        response.setDocumento("77778888");
        response.setNombres("Juan");
        response.setApellidosPaterno("Perez");
        response.setApellidosMaterno("Gomez");
        response.setSexo("M");
        response.setCreado(LocalDateTime.now());
        response.setActualizado(LocalDateTime.now());
        response.setPersona(getValidResponse());
        return response;
    }

    public static PersonaJuridicaRequestDTO getValidJuridicaRequest() {
        PersonaJuridicaRequestDTO request = new PersonaJuridicaRequestDTO();
        request.setRuc("20123456789");
        request.setRazonSocial("Empresa SAC");
        request.setPersona(getValidRequest());
        return request;
    }

    public static PersonaJuridicaResponseDTO getValidJuridicaResponse() {
        PersonaJuridicaResponseDTO response = new PersonaJuridicaResponseDTO();
        response.setId(1);
        response.setRuc("20123456789");
        response.setRazonSocial("Empresa SAC");
        response.setCreado(LocalDateTime.now());
        response.setActualizado(LocalDateTime.now());
        response.setPersona(getValidResponse());
        return response;
    }
}
