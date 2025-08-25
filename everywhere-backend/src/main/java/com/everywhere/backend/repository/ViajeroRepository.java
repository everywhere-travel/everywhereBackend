package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Viajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ViajeroRepository extends JpaRepository<Viajero, Integer> {
    List<Viajero> findByNombresContainingIgnoreCase(String nombres);
    List<Viajero> findByNumeroDocumentoContainingIgnoreCase(String numeroDocumento);
    List<Viajero> findByNacionalidadContainingIgnoreCase(String nacionalidad);
    List<Viajero> findByResidenciaContainingIgnoreCase(String residencia);
    List<Viajero> findByFechaVencimientoDocumento(LocalDate fechaVencimiento);
    List<Viajero> findByFechaVencimientoDocumentoBetween(LocalDate fechaInicio, LocalDate fechaFin);
}
