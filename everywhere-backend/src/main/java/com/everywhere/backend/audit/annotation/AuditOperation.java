package com.everywhere.backend.audit.annotation;

import java.lang.annotation.*;

/**
 * Anotación para personalizar o forzar la auditoría sobre métodos de negocio o controladores.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditOperation {
    /**
     * Nombre de la tabla o entidad (ej: "cotizaciones", "personas", "liquidaciones").
     */
    String tabla() default "";

    /**
     * Acción forzada: "CREACION", "MODIFICACION", "ELIMINACION", "CONSULTA".
     */
    String accion() default "";

    /**
     * Descripción opcional del evento.
     */
    String descripcion() default "";
}
