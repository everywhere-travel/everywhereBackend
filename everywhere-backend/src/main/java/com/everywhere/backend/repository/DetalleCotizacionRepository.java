package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Integer> {
    @Query("SELECT d FROM DetalleCotizacion d " +
            "LEFT JOIN FETCH d.producto " +
            "LEFT JOIN FETCH d.proveedor " +
            "LEFT JOIN FETCH d.categoria " +
            "LEFT JOIN FETCH d.operador " +
            "WHERE d.cotizacion.id = :cotizacionId " +
            "ORDER BY d.id")
    List<DetalleCotizacion> findByCotizacionId(@Param("cotizacionId") int cotizacionId);
    @Query("SELECT COUNT(dc) FROM DetalleCotizacion dc WHERE dc.producto.id = :productoId")
    long countByProductoId(@Param("productoId") Integer productoId);
    @Query("SELECT COUNT(dc) FROM DetalleCotizacion dc WHERE dc.proveedor.id = :proveedorId")
    long countByProveedorId(@Param("proveedorId") Integer proveedorId);

    @Query("SELECT COUNT(dc) FROM DetalleCotizacion dc WHERE dc.categoria.id = :categoriaId")
    long countByCategoriaId(@Param("categoriaId") Integer categoriaId);

}