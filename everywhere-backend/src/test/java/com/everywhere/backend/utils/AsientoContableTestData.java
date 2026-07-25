package com.everywhere.backend.utils;

import com.everywhere.backend.model.dto.AsientoContableRequestDTO;
import com.everywhere.backend.model.dto.AsientoContableResponseDTO;
import com.everywhere.backend.model.dto.DetalleAsientoContableRequestDTO;
import com.everywhere.backend.model.dto.DetalleAsientoContableResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

public class AsientoContableTestData {

    public static AsientoContableRequestDTO getValidRequest() {
        AsientoContableRequestDTO request = new AsientoContableRequestDTO();
        request.setFecha(LocalDate.now());
        request.setGlosa("Glosa de prueba");
        request.setMoneda("USD");
        request.setOrigen("LIQUIDACION");
        request.setOrigenId(1);
        request.setGeneradoAutomaticamente(false);
        
        DetalleAsientoContableRequestDTO detalle = new DetalleAsientoContableRequestDTO();
        detalle.setCuentaId(1);
        detalle.setDebe(BigDecimal.valueOf(100));
        detalle.setHaber(BigDecimal.ZERO);
        request.setDetalles(Collections.singletonList(detalle));
        return request;
    }

    public static AsientoContableResponseDTO getValidResponse() {
        AsientoContableResponseDTO response = new AsientoContableResponseDTO();
        response.setId(1);
        response.setFecha(LocalDate.now());
        response.setGlosa("Glosa de prueba");
        response.setMoneda("USD");
        response.setAnulado(false);
        response.setOrigen("LIQUIDACION");
        response.setOrigenId(1);
        response.setTotalDebe(BigDecimal.valueOf(100));
        response.setTotalHaber(BigDecimal.valueOf(100));
        response.setCreado(LocalDateTime.now());
        
        DetalleAsientoContableResponseDTO detalle = new DetalleAsientoContableResponseDTO();
        detalle.setId(1);
        detalle.setCuentaCodigo("101");
        detalle.setCuentaNombre("Caja");
        detalle.setDebe(BigDecimal.valueOf(100));
        detalle.setHaber(BigDecimal.ZERO);
        response.setDetalles(Collections.singletonList(detalle));
        
        return response;
    }
}
