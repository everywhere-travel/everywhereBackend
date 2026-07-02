package com.everywhere.backend.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación opcional para darle un nombre amigable a la operación en el log.
 * Si no se usa, el Aspecto tomará el nombre de la Clase y el Método por defecto.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperacion {
    String value() default "";
}
