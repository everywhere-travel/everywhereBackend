package com.everywhere.backend.security;

import com.everywhere.backend.exceptions.UnauthorizedAccessException;
import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.model.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthorizationService {

    public boolean hasPermission(String module, String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Usuario no autenticado intentando acceder a módulo: {}", module);
            throw new UnauthorizedAccessException("No se encuentra autenticado. Por favor, inicie sesión.");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        if (user == null) {
            log.error("UserPrincipal no contiene información del usuario");
            throw new UnauthorizedAccessException("La sesión no contiene información del usuario. Por favor, vuelva a iniciar sesión.");
        }

        if (user.getRole() == null) {
            log.warn("Usuario sin rol asignado: {}", user.getEmail());
            throw new UnauthorizedAccessException("El usuario no tiene un rol asignado. Contacte al administrador.");
        }

        String roleName = user.getRole().getName();
        log.info("Verificando permisos para usuario: {} con rol: {}", user.getEmail(), roleName);

        try {
            Role userRole = Role.fromName(roleName);
            boolean hasAccess = userRole.hasPermission(module, permission);

            log.info("Rol {} - Acceso al módulo {} con permiso {}: {}",
                    roleName, module, permission, hasAccess);

            return hasAccess;
        } catch (IllegalArgumentException e) {
            log.error("Rol no encontrado en enum: {} para usuario: {}", roleName, user.getEmail());
            throw new UnauthorizedAccessException("El rol del usuario no es válido. Contacte al administrador.");
        }
    }

    public Role getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No hay autenticación en el contexto");
            return null;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        if (user == null) {
            log.error("UserPrincipal no contiene información del usuario");
            return null;
        }

        if (user.getRole() == null) {
            log.warn("Usuario sin rol asignado: {}", user.getEmail());
            return null;
        }

        try {
            return Role.fromName(user.getRole().getName());
        } catch (IllegalArgumentException e) {
            log.error("Error al obtener rol del usuario: {}", e.getMessage());
            return null;
        }
    }

    public boolean isAdminOrSistemas() {
        Role role = getCurrentUserRole();
        return role != null && (role == Role.ADMIN || role == Role.SISTEMAS);
    }
}
