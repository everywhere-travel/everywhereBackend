package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.PersonaNatural;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaNaturalRepository extends JpaRepository<PersonaNatural, Integer> {
    List<PersonaNatural> findByDocumentoContainingIgnoreCase(String documento);
    List<PersonaNatural> findByNombresContainingIgnoreCase(String nombres);
    List<PersonaNatural> findByApellidosContainingIgnoreCase(String apellidos);
}
