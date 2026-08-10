package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
}