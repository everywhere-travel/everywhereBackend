package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Proveedor;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    boolean existsByRuc(Integer ruc);

    @Query("SELECT new com.everywhere.backend.model.dto.DropdownResponseDTO(e.id, e.nombre) FROM Proveedor e")
    List<DropdownResponseDTO> findDropdown();
}
