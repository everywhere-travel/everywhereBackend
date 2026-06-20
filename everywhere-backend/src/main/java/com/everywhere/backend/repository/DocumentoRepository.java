package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Documento;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {

    @Query("SELECT new com.everywhere.backend.model.dto.DropdownResponseDTO(e.id, e.descripcion) FROM Documento e")
    List<DropdownResponseDTO> findDropdown();
}
