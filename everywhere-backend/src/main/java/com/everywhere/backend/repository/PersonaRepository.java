package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Personas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaRepository extends JpaRepository<Personas, Integer> {
    List<Personas> findByEmailContainingIgnoreCase(String email);
    List<Personas> findByTelefonoContainingIgnoreCase(String telefono);
}
