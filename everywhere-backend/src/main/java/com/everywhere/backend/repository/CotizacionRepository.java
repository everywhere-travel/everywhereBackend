package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Integer> {

    @Query("SELECT MAX(c.id) FROM Cotizacion c")
    Integer findMaxId();
}
