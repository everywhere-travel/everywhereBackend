package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Integer> {

    List<DetalleCotizacion> findByCotizacionId(int cotizacionId);

    /**
     * Encuentra todos los detalles con sus relaciones cargadas para evitar lazy loading
     */
    @Query("SELECT dc FROM DetalleCotizacion dc " +
           "LEFT JOIN FETCH dc.cotizacion " +
           "LEFT JOIN FETCH dc.producto " +
           "LEFT JOIN FETCH dc.proveedor " +
           "LEFT JOIN FETCH dc.categoria")
    List<DetalleCotizacion> findAllWithRelations();

    /**
     * Encuentra un detalle por ID con sus relaciones cargadas para evitar lazy loading
     */
    @Query("SELECT dc FROM DetalleCotizacion dc " +
           "LEFT JOIN FETCH dc.cotizacion " +
           "LEFT JOIN FETCH dc.producto " +
           "LEFT JOIN FETCH dc.proveedor " +
           "LEFT JOIN FETCH dc.categoria " +
           "WHERE dc.id = :id")
    Optional<DetalleCotizacion> findByIdWithRelations(@Param("id") Integer id);

    /**
     * Encuentra detalles por cotización ID con sus relaciones cargadas para evitar lazy loading
     */
    @Query("SELECT dc FROM DetalleCotizacion dc " +
           "LEFT JOIN FETCH dc.cotizacion " +
           "LEFT JOIN FETCH dc.producto " +
           "LEFT JOIN FETCH dc.proveedor " +
           "LEFT JOIN FETCH dc.categoria " +
           "WHERE dc.cotizacion.id = :cotizacionId")
    List<DetalleCotizacion> findByCotizacionIdWithRelations(@Param("cotizacionId") Integer cotizacionId);
}
