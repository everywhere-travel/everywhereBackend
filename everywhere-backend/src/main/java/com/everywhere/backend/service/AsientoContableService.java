package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.AsientoContableRequestDTO;
import com.everywhere.backend.model.dto.AsientoContableResponseDTO;

import java.util.List;
import com.everywhere.backend.model.entity.PagoPax;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.model.entity.Recibo;

public interface AsientoContableService {

    List<AsientoContableResponseDTO> listar();

    AsientoContableResponseDTO obtenerPorId(Integer id);

    AsientoContableResponseDTO crear(AsientoContableRequestDTO request);

    void anular(Integer id);

    List<AsientoContableResponseDTO> listarPorOrigen(String origen, Integer origenId);

    void generarAsientoPorPagoPax(PagoPax pagoPax);

void generarAsientoPorLiquidacion(Liquidacion liquidacion);

void generarAsientoPorDocumentoCobranza(DocumentoCobranza documento);

    /**
     * Genera el asiento contable de cobro al registrar un Recibo (pago del cliente).
     * Movimiento: DEBE Caja/Banco — HABER Clientes.
     */
    void generarAsientoPorRecibo(Recibo recibo);
}