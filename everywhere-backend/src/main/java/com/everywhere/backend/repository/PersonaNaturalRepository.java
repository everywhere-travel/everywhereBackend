package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.PersonaNatural;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaNaturalRepository extends JpaRepository<PersonaNatural, Integer> {
    // Campo único - solo puede haber uno
    Optional<PersonaNatural> findByDocumentoIgnoreCase(String documento);

    // Campos que pueden repetirse - pueden haber varias personas con el mismo nombre/apellido exacto
    List<PersonaNatural> findByNombresIgnoreCase(String nombres);
    List<PersonaNatural> findByApellidosIgnoreCase(String apellidos);
}
