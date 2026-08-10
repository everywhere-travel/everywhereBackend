package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Liquidacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiquidacionRepository extends JpaRepository<Liquidacion, Integer> {

    @EntityGraph(attributePaths = {
            "producto",
            "formaPago",
            "cotizacion",
            "cotizacion.counter",
            "cotizacion.estadoCotizacion",
            "cotizacion.formaPago",
            "cotizacion.personas",
            "cotizacion.sucursal",
            "cotizacion.carpeta",
            "carpeta"
    })
    @NonNull
    @org.springframework.data.jpa.repository.Query("SELECT l FROM Liquidacion l ORDER BY l.id DESC")
    List<Liquidacion> findAll();



    @org.springframework.data.jpa.repository.Query("SELECT l.id FROM Liquidacion l ORDER BY l.id DESC")
    Page<Integer> findPageIds(Pageable pageable);


    @org.springframework.data.jpa.repository.Query("SELECT l FROM Liquidacion l " +
           "LEFT JOIN FETCH l.producto " +
           "LEFT JOIN FETCH l.formaPago " +
           "LEFT JOIN FETCH l.cotizacion cot " +
           "LEFT JOIN FETCH cot.counter " +
           "LEFT JOIN FETCH cot.estadoCotizacion " +
           "LEFT JOIN FETCH cot.formaPago " +
           "LEFT JOIN FETCH cot.personas " +
           "LEFT JOIN FETCH cot.sucursal " +
           "LEFT JOIN FETCH cot.carpeta " +
           "LEFT JOIN FETCH l.carpeta " +
           "WHERE l.id IN :ids " +
           "ORDER BY l.id DESC")
    List<Liquidacion> findByIds(@org.springframework.data.repository.query.Param("ids") List<Integer> ids);



    @EntityGraph(attributePaths = {
            "producto",
            "formaPago",
            "cotizacion",
            "cotizacion.counter",
            "cotizacion.estadoCotizacion",
            "cotizacion.formaPago",
            "cotizacion.personas",
            "cotizacion.sucursal",
            "cotizacion.carpeta",
            "carpeta"
    })
    @NonNull
    Optional<Liquidacion> findById(@NonNull Integer id);

    // Buscar liquidaciones por carpeta
    @EntityGraph(attributePaths = {
            "producto",
            "formaPago",
            "cotizacion",
            "cotizacion.counter",
            "cotizacion.estadoCotizacion",
            "cotizacion.formaPago",
            "cotizacion.personas",
            "cotizacion.sucursal",
            "cotizacion.carpeta",
            "carpeta"
    })
    List<Liquidacion> findByCarpetaId(Integer carpetaId);

    // Buscar liquidaciones sin carpeta asignada
    @EntityGraph(attributePaths = {
            "producto",
            "formaPago",
            "cotizacion",
            "cotizacion.counter",
            "cotizacion.estadoCotizacion",
            "cotizacion.formaPago",
            "cotizacion.personas",
            "cotizacion.sucursal",
            "cotizacion.carpeta",
            "carpeta"
    })
    List<Liquidacion> findByCarpetaIsNull();
}