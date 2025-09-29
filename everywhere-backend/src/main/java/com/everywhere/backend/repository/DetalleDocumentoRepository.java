package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DetalleDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleDocumentoRepository extends JpaRepository<DetalleDocumento, Integer> {
    List<DetalleDocumento> findByViajeroId(Integer viajeroId);
    List<DetalleDocumento> findByDocumentoId(Integer documentoId);
    List<DetalleDocumento> findByNumeroContainingIgnoreCase(String numero);

}
