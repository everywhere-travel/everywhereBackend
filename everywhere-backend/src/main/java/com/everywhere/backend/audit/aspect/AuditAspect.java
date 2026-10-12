package com.everywhere.backend.audit.aspect;

import com.everywhere.backend.audit.annotation.AuditOperation;
import com.everywhere.backend.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * Aspecto AOP que intercepta automáticamente operaciones de mutación en controladores REST
 * y métodos anotados con @AuditOperation para persistirlos en la tabla maestra de auditoría.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @Around("@annotation(auditOperation)")
    public Object auditAnnotatedMethod(ProceedingJoinPoint joinPoint, AuditOperation auditOperation) throws Throwable {
        Object result = joinPoint.proceed();
        try {
            String tabla = auditOperation.tabla().isEmpty() ? resolveTableName(joinPoint) : auditOperation.tabla();
            String accion = auditOperation.accion().isEmpty() ? "OPERACION" : auditOperation.accion();
            String id = extractRecordId(joinPoint, result);
            Object payload = extractPayload(joinPoint);

            auditService.registrar(tabla, id, accion, null, payload != null ? payload : result);
        } catch (Exception e) {
            log.warn("Error en AuditAspect (annotated): {}", e.getMessage());
        }
        return result;
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *) && " +
            "!within(com.everywhere.backend.audit.api.AuditoriaController) && " +
            "!within(com.everywhere.backend.api.HealthController) && " +
            "!within(com.everywhere.backend.api.AuthController) && " +
            "(execution(@org.springframework.web.bind.annotation.PostMapping * *(..)) || " +
            " execution(@org.springframework.web.bind.annotation.PutMapping * *(..)) || " +
            " execution(@org.springframework.web.bind.annotation.PatchMapping * *(..)) || " +
            " execution(@org.springframework.web.bind.annotation.DeleteMapping * *(..)))")
    public Object auditMutationControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Si ya tiene @AuditOperation, lo maneja el pointcut específico
        if (method.isAnnotationPresent(AuditOperation.class)) {
            return joinPoint.proceed();
        }

        String accion = resolveAction(method);
        String tabla = resolveTableName(joinPoint);
        Object requestBody = extractPayload(joinPoint);

        Object result = joinPoint.proceed();

        try {
            String id = extractRecordId(joinPoint, result);
            Object finalState = result instanceof ResponseEntity<?> re ? re.getBody() : result;

            if ("CREACION".equals(accion)) {
                auditService.registrar(tabla, id, accion, null, finalState != null ? finalState : requestBody);
            } else if ("ELIMINACION".equals(accion)) {
                auditService.registrar(tabla, id, accion, requestBody != null ? requestBody : ("ID " + id), null);
            } else { // MODIFICACION
                auditService.registrar(tabla, id, accion, requestBody, finalState);
            }
        } catch (Exception e) {
            log.warn("Error al registrar auditoría automática en mutación: {}", e.getMessage());
        }

        return result;
    }

    private String resolveAction(Method method) {
        if (method.isAnnotationPresent(PostMapping.class)) return "CREACION";
        if (method.isAnnotationPresent(PutMapping.class) || method.isAnnotationPresent(PatchMapping.class)) return "MODIFICACION";
        if (method.isAnnotationPresent(DeleteMapping.class)) return "ELIMINACION";
        return "MODIFICACION";
    }

    private String resolveTableName(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        // Quitar "Controller", "ServiceImpl", etc.
        className = className.replace("Controller", "").replace("ServiceImpl", "").replace("Service", "");
        return toSnakeCase(className);
    }

    private String extractRecordId(ProceedingJoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Long || arg instanceof Integer) {
                return String.valueOf(arg);
            }
            if (arg instanceof String s && s.matches("^\\d+$")) {
                return s;
            }
        }

        // Si no hay ID en los argumentos pero el resultado tiene id
        if (result instanceof ResponseEntity<?> re && re.getBody() != null) {
            try {
                Object body = re.getBody();
                Method getId = body.getClass().getMethod("getId");
                Object idVal = getId.invoke(body);
                if (idVal != null) return String.valueOf(idVal);
            } catch (Exception ignored) {}
        }

        return "0";
    }

    private Object extractPayload(ProceedingJoinPoint joinPoint) {
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        Method method = sig.getMethod();
        java.lang.annotation.Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < paramAnnotations.length; i++) {
            for (java.lang.annotation.Annotation ann : paramAnnotations[i]) {
                if (ann instanceof RequestBody) {
                    return args[i];
                }
            }
        }

        // Si no hay @RequestBody, retornar primer objeto no primitivo
        for (Object arg : args) {
            if (arg != null && !(arg instanceof Number) && !(arg instanceof String) && !(arg instanceof Boolean)) {
                return arg;
            }
        }
        return null;
    }

    private String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
