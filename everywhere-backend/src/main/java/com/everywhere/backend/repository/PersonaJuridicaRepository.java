package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.PersonaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaJuridicaRepository extends JpaRepository<PersonaJuridica, Integer> {
    // Campo único - solo puede haber uno
    Optional<PersonaJuridica> findByRucIgnoreCase(String ruc);

    // Campo que puede repetirse - pueden haber varias empresas con la misma razón social exacta
    List<PersonaJuridica> findByRazonSocialIgnoreCase(String razonSocial);
}
