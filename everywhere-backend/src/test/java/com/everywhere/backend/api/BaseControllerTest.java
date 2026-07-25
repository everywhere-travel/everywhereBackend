package com.everywhere.backend.api;

import com.everywhere.backend.exceptions.GlobalExceptionHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

public abstract class BaseControllerTest {

    protected MockMvc mockMvc;

    /**
     * Configura el MockMvc en modo standalone para pruebas unitarias rápidas.
     * 
     * @param controller El controlador bajo prueba.
     */
    protected void setupMockMvc(Object controller) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    
    /**
     * Configura el MockMvc en modo standalone con un validador personalizado (útil si hay problemas con Hibernate Validator en standalone).
     */
    protected void setupMockMvcWithValidator(Object controller, Validator validator) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
}
