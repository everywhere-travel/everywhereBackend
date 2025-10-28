package com.everywhere.backend.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }
}
/*
 * https://www.arquitecturajava.com/controlleradvice-en-spring-boot-manejo-global-de-errores/#%C2%BFQue_es_ControllerAdvice
 * 
 */