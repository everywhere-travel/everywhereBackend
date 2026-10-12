package com.everywhere.backend.audit;

import com.everywhere.backend.audit.entity.Auditoria;
import com.everywhere.backend.audit.repository.AuditoriaRepository;
import com.everywhere.backend.audit.service.AuditService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class AuditoriaDbTest {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private AuditService auditService;

    @Test
    void testDbConnectionAndAuditoria() {
        System.out.println("=== PROBANDO REGISTRAR AUDITORIA EN BD ===");
        auditService.registrar("test", "1", "CREACION", null, "{\"msg\":\"hola\"}");
        System.out.println("=== REGISTRO EXITOSO ===");

        System.out.println("=== PROBANDO LISTAR CON ESPECIFICACION (FILTROS NULL) ===");
        var page = auditService.listar(null, null, null, null, null, null, PageRequest.of(0, 10));
        assertNotNull(page);
        System.out.println("=== QUERY EXITOSA: " + page.getTotalElements() + " elementos ===");
    }
}
