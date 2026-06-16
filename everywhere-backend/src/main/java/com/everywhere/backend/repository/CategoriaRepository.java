package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Categoria;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    boolean existsByNombreIgnoreCase(String nombre);

    @Query("SELECT new com.everywhere.backend.model.dto.DropdownResponseDTO(e.id, e.nombre) FROM Categoria e")
    List<DropdownResponseDTO> findDropdown();
}
