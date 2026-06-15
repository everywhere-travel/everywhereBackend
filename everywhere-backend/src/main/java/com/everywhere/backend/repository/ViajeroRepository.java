package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Viajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List; 

import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface ViajeroRepository extends JpaRepository<Viajero, Integer> {

    @Override
    @EntityGraph(attributePaths = {"personaNatural", "personaNatural.personas", "personaNatural.categoriaPersona"})
    List<Viajero> findAll();

    @Query(value = "SELECT * FROM viajeros WHERE UPPER(TRANSLATE(via_nacio_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:nacionalidad, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<Viajero> findByNacionalidadIgnoreAccents(@Param("nacionalidad") String nacionalidad);

    @Query(value = "SELECT * FROM viajeros WHERE UPPER(TRANSLATE(via_resi_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:residencia, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<Viajero> findByResidenciaIgnoreAccents(@Param("residencia") String residencia);
}