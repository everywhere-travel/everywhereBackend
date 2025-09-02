package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Integer> {

    List<DetalleCotizacion> findByCotizacionId(int cotizacionId);
}
