package com.everywhere.backend.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.everywhere.backend.model.dto.ReciboResponseDTO;
import com.everywhere.backend.model.dto.ReciboUpdateDTO;

public interface ReciboService {
    /**
     * Crea un nuevo Recibo a partir de un DocumentoCobranza.
     * Un mismo DocumentoCobranza puede tener múltiples recibos (pagos parciales o totales).
     */
    ReciboResponseDTO createRecibo(Integer documentoCobranzaId, Integer personaJuridicaId, Integer sucursalId, java.math.BigDecimal montoPago);

    ByteArrayInputStream generatePdf(Integer reciboId);

    ReciboResponseDTO findById(Integer id);

    ReciboResponseDTO findBySerieAndCorrelativo(String serie, Integer correlativo);

    List<ReciboResponseDTO> findAll();

    /** Retorna todos los recibos vinculados a un DocumentoCobranza. */
    List<ReciboResponseDTO> findByDocumentoCobranzaId(Integer documentoCobranzaId);

    /** Retorna todos los recibos vinculados a una cotización (puede haber varios). */
    List<ReciboResponseDTO> findByCotizacionId(Integer cotizacionId);

    ReciboResponseDTO patchRecibo(Integer id, ReciboUpdateDTO reciboUpdateDTO);

    List<ReciboResponseDTO> findByCarpeta(Integer carpetaId);

    List<ReciboResponseDTO> findSinCarpeta();

    ReciboResponseDTO updateCarpeta(Integer id, Integer carpetaId);

    /**
     * Calcula el total pagado (suma de todos los detalles de recibos)
     * para un DocumentoCobranza específico.
     */
    java.math.BigDecimal calcularTotalPagado(Integer documentoCobranzaId);
}
