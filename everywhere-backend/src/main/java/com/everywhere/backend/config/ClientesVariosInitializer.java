package com.everywhere.backend.config;

import com.everywhere.backend.model.dto.PersonaDisplayDto;
import com.everywhere.backend.service.PersonaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Inicializador que asegura la existencia del cliente genérico 'CLIENTES VARIOS' (DNI: 00000000)
 * para cotizaciones rápidas y clientes ocasionales sin almacenamiento de datos personales (Ley N° 29733).
 */
@Slf4j
@Component
@Order(20)
@RequiredArgsConstructor
public class ClientesVariosInitializer implements CommandLineRunner {

    private final PersonaService personaService;

    @Override
    public void run(String... args) {
        try {
            PersonaDisplayDto clientesVarios = personaService.getOrCreateClientesVarios();
            log.info("[Ley 29733] Cliente genérico CLIENTES VARIOS asegurado (ID: {}, Doc: {})", 
                    clientesVarios.getId(), clientesVarios.getIdentificador());
        } catch (Exception e) {
            log.warn("No se pudo inicializar CLIENTES VARIOS: {}", e.getMessage());
        }
    }
}
