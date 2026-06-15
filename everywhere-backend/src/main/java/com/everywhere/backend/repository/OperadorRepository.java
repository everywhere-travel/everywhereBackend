package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Operador;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List; 
import org.springframework.data.jpa.repository.JpaRepository;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import java.util.List;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OperadorRepository extends JpaRepository<Operador, Integer> {
    Optional<Operador> findByNombre(String nombre);
    List<Operador> nombre(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);

    @Query("SELECT new com.everywhere.backend.model.dto.DropdownResponseDTO(e.id, e.nombre) FROM Operador e")
    List<DropdownResponseDTO> findDropdown();
}
