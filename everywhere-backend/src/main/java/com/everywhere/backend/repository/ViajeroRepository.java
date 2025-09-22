package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Viajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ViajeroRepository extends JpaRepository<Viajero, Integer> {
    // Campo único - solo puede haber uno
    Optional<Viajero> findByNumeroDocumentoIgnoreCase(String numeroDocumento);

    // Método para verificar si existe un número de documento (para validación)
    boolean existsByNumeroDocumentoIgnoreCase(String numeroDocumento);

    // Búsquedas que ignoran tildes/acentos usando LIKE para búsquedas parciales
    @Query(value = "SELECT * FROM viajeros WHERE UPPER(TRANSLATE(via_nomb_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(CONCAT('%', :nombres, '%'), 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<Viajero> findByNombresIgnoreAccents(@Param("nombres") String nombres);

    @Query(value = "SELECT * FROM viajeros WHERE UPPER(TRANSLATE(via_nacio_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:nacionalidad, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<Viajero> findByNacionalidadIgnoreAccents(@Param("nacionalidad") String nacionalidad);

    @Query(value = "SELECT * FROM viajeros WHERE UPPER(TRANSLATE(via_resi_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:residencia, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<Viajero> findByResidenciaIgnoreAccents(@Param("residencia") String residencia);

    // Métodos originales mantenidos para compatibilidad
    List<Viajero> findByNombresContainingIgnoreCase(String nombres);
    List<Viajero> findByNacionalidadIgnoreCase(String nacionalidad);
    List<Viajero> findByResidenciaIgnoreCase(String residencia);

    // Búsquedas por fecha (pueden devolver múltiples resultados)
    List<Viajero> findByFechaVencimientoDocumento(LocalDate fechaVencimiento);
    List<Viajero> findByFechaVencimientoDocumentoBetween(LocalDate fechaInicio, LocalDate fechaFin);

}
