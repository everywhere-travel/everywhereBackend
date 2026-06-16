package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.PersonaNatural;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface PersonaNaturalRepository extends JpaRepository<PersonaNatural, Integer> {

    @Override
    @EntityGraph(attributePaths = {"personas", "viajero", "categoriaPersona"})
    List<PersonaNatural> findAll();
    Optional<PersonaNatural> findByDocumentoIgnoreCase(String documento);
    Optional<PersonaNatural> findByPersonasId(Integer personaId);
    List<PersonaNatural> findByPersonasIdIn(List<Integer> personasIds);

    @Query(value = "SELECT * FROM persona_natural WHERE UPPER(TRANSLATE(per_nat_nomb_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:nombres, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<PersonaNatural> findByNombresIgnoreAccents(@Param("nombres") String nombres);

    @Query(value = "SELECT * FROM persona_natural WHERE UPPER(TRANSLATE(per_nat_apell_pat_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:apellidosPaterno, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<PersonaNatural> findByApellidosPaternoIgnoreAccents(@Param("apellidosPaterno") String apellidosPaterno);

    @Query(value = "SELECT * FROM persona_natural WHERE UPPER(TRANSLATE(per_nat_apell_mat_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:apellidosMaterno, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<PersonaNatural> findByApellidosMaternoIgnoreAccents(@Param("apellidosMaterno") String apellidosMaterno);

    List<PersonaNatural> findByCategoriaPersonaId(Integer categoriaId);
    Optional<PersonaNatural> findByDocumentoIgnoreCaseAndIdNot(String documento, Integer id);
    @Query("SELECT COUNT(pn) FROM PersonaNatural pn WHERE pn.categoriaPersona.id = :categoriaId")
    long countByCategoriaPersonaId(@Param("categoriaId") Integer categoriaId);

    @Query("SELECT pn FROM PersonaNatural pn JOIN FETCH pn.personas LEFT JOIN FETCH pn.categoriaPersona LEFT JOIN FETCH pn.viajero WHERE pn.id IN :ids ORDER BY pn.id DESC")
    List<PersonaNatural> findConDetalles(@Param("ids") List<Integer> ids);
    
    @EntityGraph(attributePaths = {"personas", "viajero", "categoriaPersona"})
    List<PersonaNatural> findTop100ByOrderByIdDesc();
}