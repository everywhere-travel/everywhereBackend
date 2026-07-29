package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface DetalleDocumentoCobranzaRepository extends JpaRepository<DetalleDocumentoCobranza, Long> {

    @Query("SELECT d FROM DetalleDocumentoCobranza d WHERE d.documentoCobranza.id = :documentoId")
    List<DetalleDocumentoCobranza> findByDocumentoCobranzaId(Long documentoId);

    @Query("SELECT d FROM DetalleDocumentoCobranza d WHERE d.producto.id = :productoId")
    List<DetalleDocumentoCobranza> findByProductoId(Long productoId);

    /**
     * Calcula el total de deuda (sum(cantidad * precio)) agrupado por documento_cobranza_id.
     * Retorna una lista de Object[] con [documentoCobranzaId, totalDeuda].
     * Usar para bulk load y evitar el problema N+1.
     */
    @Query("SELECT d.documentoCobranza.id, COALESCE(SUM(d.cantidad * d.precio), 0) " +
           "FROM DetalleDocumentoCobranza d " +
           "WHERE d.documentoCobranza.id IN :documentoIds " +
           "GROUP BY d.documentoCobranza.id")
    List<Object[]> findTotalDeudaByDocumentoIds(@Param("documentoIds") List<Long> documentoIds);

    // Métodos sin lazy loading
    @Query("SELECT DISTINCT d FROM DetalleDocumentoCobranza d " +
           "LEFT JOIN FETCH d.documentoCobranza " +
           "LEFT JOIN FETCH d.producto")
    List<DetalleDocumentoCobranza> findAllWithRelations();

    @Query("SELECT DISTINCT d FROM DetalleDocumentoCobranza d " +
           "LEFT JOIN FETCH d.documentoCobranza " +
           "LEFT JOIN FETCH d.producto " +
           "WHERE d.id = :id")
    Optional<DetalleDocumentoCobranza> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT DISTINCT d FROM DetalleDocumentoCobranza d " +
           "LEFT JOIN FETCH d.documentoCobranza " +
           "LEFT JOIN FETCH d.producto " +
           "WHERE d.documentoCobranza.id = :documentoId")
    List<DetalleDocumentoCobranza> findByDocumentoCobranzaIdWithRelations(@Param("documentoId") Long documentoId);
}