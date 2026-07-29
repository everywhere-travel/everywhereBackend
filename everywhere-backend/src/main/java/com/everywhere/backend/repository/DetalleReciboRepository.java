package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DetalleRecibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleReciboRepository extends JpaRepository<DetalleRecibo, Integer> {

    @Query("SELECT d FROM DetalleRecibo d WHERE d.recibo.id = :reciboId")
    List<DetalleRecibo> findByReciboId(@Param("reciboId") Integer reciboId);

    @Query("SELECT d FROM DetalleRecibo d WHERE d.producto.id = :productoId")
    List<DetalleRecibo> findByProductoId(@Param("productoId") Long productoId);

    /**
     * Calcula el total pagado (sum(cantidad * precio)) por recibo_id en bulk.
     * Retorna Object[] con [reciboId, totalPagado].
     */
    @Query("SELECT d.recibo.id, COALESCE(SUM(d.cantidad * d.precio), 0) " +
           "FROM DetalleRecibo d " +
           "WHERE d.recibo.id IN :reciboIds " +
           "GROUP BY d.recibo.id")
    List<Object[]> findTotalPagadoByReciboIds(@Param("reciboIds") List<Integer> reciboIds);

    /**
     * Calcula el total pagado acumulado por documentoCobranza_id en bulk.
     * Retorna Object[] con [documentoCobranzaId, totalPagado].
     */
    @Query("SELECT d.recibo.documentoCobranza.id, COALESCE(SUM(d.cantidad * d.precio), 0) " +
           "FROM DetalleRecibo d " +
           "WHERE d.recibo.documentoCobranza.id IN :documentoIds " +
           "GROUP BY d.recibo.documentoCobranza.id")
    List<Object[]> findTotalPagadoByDocumentoCobranzaIds(@Param("documentoIds") List<Long> documentoIds);

    // Métodos sin lazy loading
    @Query("SELECT DISTINCT d FROM DetalleRecibo d " +
           "LEFT JOIN FETCH d.recibo " +
           "LEFT JOIN FETCH d.producto")
    List<DetalleRecibo> findAllWithRelations();

    @Query("SELECT DISTINCT d FROM DetalleRecibo d " +
           "LEFT JOIN FETCH d.recibo " +
           "LEFT JOIN FETCH d.producto " +
           "WHERE d.id = :id")
    Optional<DetalleRecibo> findByIdWithRelations(@Param("id") Integer id);

    @Query("SELECT DISTINCT d FROM DetalleRecibo d " +
           "LEFT JOIN FETCH d.recibo " +
           "LEFT JOIN FETCH d.producto " +
           "WHERE d.recibo.id = :reciboId")
    List<DetalleRecibo> findByReciboIdWithRelations(@Param("reciboId") Integer reciboId);
}
