package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.CategoriaPersona;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoriaPersonaRepository extends JpaRepository<CategoriaPersona, Integer> {
    
    // Buscar por nombre (case insensitive)
    @Query("SELECT cp FROM CategoriaPersona cp WHERE LOWER(cp.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<CategoriaPersona> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
    
    Optional<CategoriaPersona> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);

    @Query("SELECT new com.everywhere.backend.model.dto.DropdownResponseDTO(e.id, e.nombre) FROM CategoriaPersona e")
    List<DropdownResponseDTO> findDropdown();
}
