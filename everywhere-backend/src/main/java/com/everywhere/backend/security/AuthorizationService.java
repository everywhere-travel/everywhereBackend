package com.everywhere.backend.security;

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
            return false;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        if (user.getRole() == null) {
            log.warn("Usuario sin rol asignado: {}", user.getEmail());
            return false;
        }

        String roleName = user.getRole().getName();
        log.info("Verificando permisos para usuario: {} con rol: {}", user.getEmail(), roleName);

        try {
            Role userRole = Role.fromName(roleName);
            boolean hasModuleAccess = userRole.hasModuleAccess(module);
            boolean hasPermissionAccess = userRole.hasPermission(permission);

            log.info("Rol {} - Acceso al módulo {}: {}, Permiso {}: {}",
                    roleName, module, hasModuleAccess, permission, hasPermissionAccess);

            return userRole.canAccess(module, permission);
        } catch (IllegalArgumentException e) {
            log.error("Rol no encontrado en enum: {} para usuario: {}", roleName, user.getEmail());
            return false;
        }
    }

    public Role getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        if (user.getRole() == null) {
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
        return role == Role.ADMIN || role == Role.SISTEMAS;
    }
}
