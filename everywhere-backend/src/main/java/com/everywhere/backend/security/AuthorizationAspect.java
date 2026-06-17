package com.everywhere.backend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import org.aspectj.lang.reflect.MethodSignature;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationAspect {

    private final AuthorizationService authorizationService;

    @Around("@annotation(com.everywhere.backend.security.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequirePermission requirePermission = signature.getMethod().getAnnotation(RequirePermission.class);
        
        String module = requirePermission.module();
        String permission = requirePermission.permission();

        String currentRole = authorizationService.getCurrentUserRoleName();
        log.info("Verificando permisos — módulo: '{}', acción: '{}', rol actual: '{}'", module, permission, currentRole);

        try {
            authorizationService.hasPermission(module, permission);
        } catch (Exception e) {
            log.warn("Acceso denegado - módulo: '{}', acción: '{}', rol: '{}'", module, permission, currentRole);
            throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No tienes permisos para realizar esta acción en el módulo: " + module
            );
        }

        log.info("Acceso permitido - módulo: '{}', acción: '{}'", module, permission);
        return joinPoint.proceed();
    }
}
