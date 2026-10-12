package com.everywhere.backend.audit.repository;

import com.everywhere.backend.audit.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long>, JpaSpecificationExecutor<Auditoria> {

    @Query("SELECT DISTINCT a.tabla FROM Auditoria a ORDER BY a.tabla ASC")
    List<String> findDistinctTablas();

    long countByFechaHoraAfter(LocalDateTime fecha);

    long countByAccion(String accion);
}
