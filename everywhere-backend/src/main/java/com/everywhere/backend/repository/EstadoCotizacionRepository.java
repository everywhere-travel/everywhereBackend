package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.EstadoCotizacion;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoCotizacionRepository extends JpaRepository<EstadoCotizacion,Integer> {

    @Query("SELECT new com.everywhere.backend.model.dto.DropdownResponseDTO(e.id, e.descripcion) FROM EstadoCotizacion e")
    List<DropdownResponseDTO> findDropdown();
}
