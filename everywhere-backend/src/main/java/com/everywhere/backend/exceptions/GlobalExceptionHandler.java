package com.everywhere.backend.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.persistence.EntityNotFoundException;
import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Acceso denegado. Credenciales inválidas o sesión expirada.");
        problemDetail.setTitle("No autorizado");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedAccessException(UnauthorizedAccessException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Acceso no autorizado");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ProblemDetail> handleNullPointerException(NullPointerException ex, WebRequest request) {
        String detail;
        if (ex.getMessage() != null && ex.getMessage().contains("user") && ex.getMessage().contains("getRole")) {
            detail = "Usuario no autenticado correctamente. Por favor, vuelva a iniciar sesión.";
        } else if (ex.getMessage() != null && ex.getMessage().contains("user")) {
            detail = "Error de autenticación. El usuario no está disponible en el contexto de seguridad.";
        } else {
            detail = "Ocurrió un error interno. Por favor, contacte al administrador del sistema.";
        }
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, detail);
        problemDetail.setTitle("Error interno");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler({ ResourceNotFoundException.class, EntityNotFoundException.class })
    public ResponseEntity<ProblemDetail> handleNotFoundException(Exception ex, WebRequest request) {
        String detailMessage = ex instanceof EntityNotFoundException ? "El recurso solicitado no fue encontrado." : ex.getMessage();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detailMessage);
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler({ BadRequestException.class, IllegalArgumentException.class })
    public ResponseEntity<ProblemDetail> handleBadRequestException(Exception ex, WebRequest request) {
        String detailMessage = ex instanceof IllegalArgumentException ? "La solicitud contiene parámetros inválidos o mal formados." : ex.getMessage();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detailMessage);
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        ex.printStackTrace();
        String detailedMessage = ex.getMostSpecificCause().getMessage();
        String userFriendlyMessage;
        
        // Detectar errores de llave foránea (foreign key constraint)
        if (detailedMessage != null && (detailedMessage.contains("llave foránea") || detailedMessage.contains("foreign key"))) {
            if (detailedMessage.toLowerCase().contains("delete") || detailedMessage.toLowerCase().contains("eliminar") || detailedMessage.contains("violates foreign key constraint") && detailedMessage.contains("on table") && !detailedMessage.toLowerCase().contains("insert") && !detailedMessage.toLowerCase().contains("update")) {
                String tableName = "otro registro";
                if (detailedMessage.contains("«documento_cobranza»")) {
                    tableName = "documentos de cobranza";
                } else if (detailedMessage.contains("«") && detailedMessage.contains("»")) {
                    int start = detailedMessage.lastIndexOf("«") + 1;
                    int end = detailedMessage.lastIndexOf("»");
                    if (start > 0 && end > start) {
                        tableName = detailedMessage.substring(start, end).replace("_", " ");
                    }
                }
                userFriendlyMessage = String.format("No se puede eliminar este registro porque está siendo utilizado por %s. " +
                        "Primero debe eliminar las referencias asociadas.", tableName);
            } else {
                userFriendlyMessage = "No se puede crear o guardar el registro porque hace referencia a un elemento no existente en la base de datos (llave foránea inválida). Detalle técnico: " + detailedMessage;
            }
        } else if (detailedMessage != null && (detailedMessage.toLowerCase().contains("_pkey") || detailedMessage.toLowerCase().contains("primary key"))) {
            userFriendlyMessage = "Conflicto de numeración interna en la base de datos (secuencia de ID desincronizada). El sistema está autocorrigiendo la secuencia, por favor intente de nuevo.";
        } else if (detailedMessage != null && (detailedMessage.toLowerCase().contains("duplicate") || 
                   detailedMessage.toLowerCase().contains("ya existe"))) {
            userFriendlyMessage = "Ya existe un registro con estos datos. No se permiten duplicados.";
        } else {
            userFriendlyMessage = "No se puede completar la operación debido a restricciones de integridad de datos.";
        }
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, userFriendlyMessage);
        problemDetail.setTitle("Conflicto de integridad");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        problemDetail.setTitle(ex.getReason());
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(ex.getStatusCode()).body(problemDetail);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String tipo = "desconocido";
        Class<?> requiredType = ex.getRequiredType();
        if (requiredType != null) tipo = requiredType.getSimpleName();
        
        String detail = String.format("El parámetro '%s' debe ser de tipo %s. Valor recibido: '%s'", ex.getName(), tipo, ex.getValue());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problemDetail.setTitle("Parámetro inválido");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, WebRequest request) {
        String detail = String.format("Falta el parámetro requerido en la petición: '%s' de tipo %s", ex.getParameterName(), ex.getParameterType());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problemDetail.setTitle("Parámetro faltante");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "No resource found for " + ex.getResourcePath());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(InsufficientStorageException.class)
    public ResponseEntity<ProblemDetail> handleInsufficientStorageException(InsufficientStorageException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(507), ex.getMessage());
        problemDetail.setTitle("Insufficient Storage");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(507).body(problemDetail);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ProblemDetail> handleMaxSizeException(MaxUploadSizeExceededException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.PAYLOAD_TOO_LARGE, 
            "El tamaño total de los archivos adjuntos supera el límite permitido (20MB). Por favor, envíe archivos más pequeños.");
        problemDetail.setTitle("Archivos demasiado grandes");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(problemDetail);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ProblemDetail> handleDataAccessException(DataAccessException ex, WebRequest request) {
        ex.printStackTrace();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un problema al acceder a la base de datos. Comuníquese con el área de soporte.");
        problemDetail.setTitle("Error de base de datos");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno. Por favor, contacte al administrador del sistema.");
        problemDetail.setTitle("Error interno");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflictException(ConflictException ex, WebRequest request) {
        ex.printStackTrace();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Conflict");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setInstance(URI.create(ex.getInstance() != null ? ex.getInstance() : request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }
}
