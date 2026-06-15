package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.stereotype.Repository;
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

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, Integer> {
    Optional<FormaPago> findByCodigo(Integer codigo); 
    boolean existsByCodigo(Integer codigo);
    List<FormaPago> findByDescripcionContainingIgnoreCase(String descripcion);
    Optional<FormaPago> findByDescripcionIgnoreCase(String descripcion);

    @Query("SELECT new com.everywhere.backend.model.dto.DropdownResponseDTO(e.id, e.descripcion) FROM FormaPago e")
    List<DropdownResponseDTO> findDropdown();
}
