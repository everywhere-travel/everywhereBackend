package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.PersonaNatural;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaNaturalRepository extends JpaRepository<PersonaNatural, Integer> {
    // Campo único - solo puede haber uno
    Optional<PersonaNatural> findByDocumentoIgnoreCase(String documento);

    // Búsquedas que ignoran tildes/acentos
    @Query(value = "SELECT * FROM persona_natural WHERE UPPER(TRANSLATE(per_nat_nomb_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:nombres, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<PersonaNatural> findByNombresIgnoreAccents(@Param("nombres") String nombres);

    @Query(value = "SELECT * FROM persona_natural WHERE UPPER(TRANSLATE(per_nat_apell_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:apellidos, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<PersonaNatural> findByApellidosIgnoreAccents(@Param("apellidos") String apellidos);

    // Métodos originales mantenidos para compatibilidad
    List<PersonaNatural> findByNombresIgnoreCase(String nombres);
    List<PersonaNatural> findByApellidosIgnoreCase(String apellidos);
}
