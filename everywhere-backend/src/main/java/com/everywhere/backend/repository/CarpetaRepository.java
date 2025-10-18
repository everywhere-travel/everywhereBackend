package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Carpeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CarpetaRepository extends JpaRepository<Carpeta, Integer> {
    // Listar carpetas por hijo de un ID padre
    List<Carpeta> findByCarpetaPadreId(Integer carpetaPadreId);

    // Listar carpetas por nivel
    List<Carpeta> findByNivel(Integer nivel);

    // Listar carpetas por nombre (contiene, ignorando mayúsculas/minúsculas)
    List<Carpeta> findByNombreContainingIgnoreCase(String nombre);

    // Listar carpetas creadas en un rango de fechas (ejemplo: año/mes o entre dos días)
    List<Carpeta> findByCreadoBetween(LocalDateTime inicio, LocalDateTime fin);

    // Listar carpetas creadas en un rango, ordenadas por fecha ascendente
    List<Carpeta> findByCreadoBetweenOrderByCreadoAsc(LocalDateTime inicio, LocalDateTime fin);

    // Carpeta raíz (sin padre)
    List<Carpeta> findByCarpetaPadreIsNull();

    // Listar carpetas recientes (luego se limita con Pageable en el service)
    List<Carpeta> findAllByOrderByCreadoDesc();

}
