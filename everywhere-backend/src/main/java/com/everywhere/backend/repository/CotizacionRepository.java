package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Integer> {
    @Override
    @Query("SELECT c FROM Cotizacion c " +
           "LEFT JOIN FETCH c.counter " +
           "LEFT JOIN FETCH c.estadoCotizacion " +
           "LEFT JOIN FETCH c.formaPago " +
           "LEFT JOIN FETCH c.personas " +
           "LEFT JOIN FETCH c.sucursal " +
           "LEFT JOIN FETCH c.carpeta " +
           "ORDER BY c.id DESC")
    List<Cotizacion> findAll();

    @Query("SELECT c.id FROM Cotizacion c ORDER BY c.id DESC")
    Page<Integer> findPageIds(Pageable pageable);


    @Query("SELECT c FROM Cotizacion c " +
           "LEFT JOIN FETCH c.counter " +
           "LEFT JOIN FETCH c.estadoCotizacion " +
           "LEFT JOIN FETCH c.formaPago " +
           "LEFT JOIN FETCH c.personas " +
           "LEFT JOIN FETCH c.sucursal " +
           "LEFT JOIN FETCH c.carpeta " +
           "WHERE c.id IN :ids " +
           "ORDER BY c.id DESC")
    List<Cotizacion> findByIds(@Param("ids") List<Integer> ids);

    @Query("SELECT MAX(c.id) FROM Cotizacion c")
    Integer findMaxId();

    @Query("SELECT c FROM Cotizacion c WHERE NOT EXISTS " +
           "(SELECT 1 FROM Liquidacion l WHERE l.cotizacion = c)")
    List<Cotizacion> findCotizacionesSinLiquidacion();

    @Query("SELECT c FROM Cotizacion c WHERE NOT EXISTS " +
           "(SELECT 1 FROM DocumentoCobranza d WHERE d.cotizacion = c)")
    List<Cotizacion> findCotizacionesSinDocumentoCobranza();

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.formaPago.id = :formaPagoId")
    long countByFormaPagoId(@Param("formaPagoId") Integer formaPagoId);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.estadoCotizacion.id = :estado")
    long countByEstadoCotizacionId(@Param("estado") int estado);

    List<Cotizacion> findByid(int id);

    List<Cotizacion> findByCarpetaId(Integer carpetaId);

    List<Cotizacion> findByCarpetaIsNull();

    // ─── Analytics: Funnel de Conversión ────────────────────────────────────────

    /**
     * Cotizaciones creadas en un rango de fechas, con todas las relaciones
     * necesarias para calcular el funnel de conversión.
     */
    @Query("SELECT c FROM Cotizacion c " +
           "LEFT JOIN FETCH c.counter " +
           "LEFT JOIN FETCH c.estadoCotizacion " +
           "LEFT JOIN FETCH c.personas " +
           "WHERE c.fechaEmision >= :start AND c.fechaEmision <= :end")
    List<Cotizacion> findByFechaEmisionBetween(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /**
     * Cotizaciones creadas en el período que YA tienen un DocumentoCobranza
     * (es decir, fueron convertidas a venta formal).
     */
    @Query("SELECT c FROM Cotizacion c " +
           "LEFT JOIN FETCH c.counter " +
           "WHERE c.fechaEmision >= :start AND c.fechaEmision <= :end " +
           "AND EXISTS (SELECT d FROM DocumentoCobranza d WHERE d.cotizacion = c)")
    List<Cotizacion> findConvertedByFechaEmision(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /**
     * Cotizaciones creadas en el período con filtro opcional de counter.
     */
    @Query("SELECT c FROM Cotizacion c " +
           "LEFT JOIN FETCH c.counter ct " +
           "LEFT JOIN FETCH c.estadoCotizacion " +
           "LEFT JOIN FETCH c.personas " +
           "WHERE c.fechaEmision >= :start AND c.fechaEmision <= :end " +
           "AND (:counterId IS NULL OR ct.id = :counterId)")
    List<Cotizacion> findByFechaEmisionBetweenFiltered(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("counterId") Integer counterId
    );
}