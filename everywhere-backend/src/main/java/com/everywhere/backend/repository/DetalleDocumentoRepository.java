package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DetalleDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleDocumentoRepository extends JpaRepository<DetalleDocumento, Integer> {
}
