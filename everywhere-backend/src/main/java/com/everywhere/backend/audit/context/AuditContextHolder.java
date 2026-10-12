package com.everywhere.backend.audit.context;

import lombok.Builder;
import lombok.Data;

/**
 * Almacena los metadatos de la petición HTTP actual en el hilo de ejecución (ThreadLocal).
 * Permite que cualquier servicio o interceptor capture IP real, endpoint y user-agent.
 */
public class AuditContextHolder {

    @Data
    @Builder
    public static class AuditMetadata {
        private String ipOrigen;
        private String userAgent;
        private String endpoint;
    }

    private static final ThreadLocal<AuditMetadata> CONTEXT = new ThreadLocal<>();

    public static void set(AuditMetadata metadata) {
        CONTEXT.set(metadata);
    }

    public static AuditMetadata get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
