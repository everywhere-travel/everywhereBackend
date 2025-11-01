package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Liquidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiquidacionRepository extends JpaRepository<Liquidacion, Integer> {

    @Query("SELECT l FROM Liquidacion l " +
           "LEFT JOIN FETCH l.producto " +
           "LEFT JOIN FETCH l.formaPago " +
           "LEFT JOIN FETCH l.cotizacion " +
           "LEFT JOIN FETCH l.carpeta " +
           "LEFT JOIN FETCH l.observacionesLiquidacion")
    List<Liquidacion> findAllWithRelations();

    @Query("SELECT l FROM Liquidacion l " +
           "LEFT JOIN FETCH l.producto " +
           "LEFT JOIN FETCH l.formaPago " +
           "LEFT JOIN FETCH l.cotizacion " +
           "LEFT JOIN FETCH l.carpeta " +
           "LEFT JOIN FETCH l.observacionesLiquidacion " +
           "WHERE l.id = :id")
    Optional<Liquidacion> findByIdWithRelations(@Param("id") Integer id);
}