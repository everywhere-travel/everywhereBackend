package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Viajero;
import org.springframework.data.jpa.repository.JpaRepository;
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

    // Campos que pueden repetirse - usar Containing para nombres (permite búsquedas parciales)
    List<Viajero> findByNombresContainingIgnoreCase(String nombres);
    List<Viajero> findByNacionalidadIgnoreCase(String nacionalidad);
    List<Viajero> findByResidenciaIgnoreCase(String residencia);

    // Búsquedas por fecha (pueden devolver múltiples resultados)
    List<Viajero> findByFechaVencimientoDocumento(LocalDate fechaVencimiento);
    List<Viajero> findByFechaVencimientoDocumentoBetween(LocalDate fechaInicio, LocalDate fechaFin);
}
