package com.everywhere.backend.security;

import com.everywhere.backend.exceptions.UnauthorizedAccessException;
import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationService {

    private final RolePermissionRepository rolePermissionRepository;

    /**
     * Verifica si el usuario autenticado tiene permiso para ejecutar una acción
     * sobre un módulo dado.
     *
     * La convención de permisos en BD es "MODULO:ACCION", por ejemplo:
     *   - "CLIENTES:READ", "COTIZACIONES:CREATE"
     *   - "ALL_MODULES:READ"  ← comodín que da acceso a todo
     *
     * @param module    Nombre del módulo (ej: "CLIENTES")
     * @param action    Acción requerida (ej: "READ", "CREATE", "UPDATE", "DELETE")
     */
    public boolean hasPermission(String module, String action) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Usuario no autenticado intentando acceder al módulo: {}", module);
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

        Integer roleId = user.getRole().getId();
        String roleName = user.getRole().getName();

        log.info("Verificando permisos para usuario: {} con rol: {} (id={})", user.getEmail(), roleName, roleId);

        // Carga todos los permisos del rol desde BD (solo los nombres, eficiente)
        Set<String> permissions = rolePermissionRepository.findPermissionNamesByRoleId(roleId);

        log.info("Permisos del rol {}: {}", roleName, permissions);

        // Permiso comodín: acceso total a todos los módulos con la acción dada
        boolean hasWildcard = permissions.contains("ALL_MODULES:" + action);

        // Permiso específico del módulo
        String requiredPermission = module + ":" + action;
        boolean hasSpecific = permissions.contains(requiredPermission);

        boolean hasAccess = hasWildcard || hasSpecific;

        // Bootstrap access for highest roles to manage Users and Roles if they don't have explicit permissions yet
        if (!hasAccess && (module.equals("USUARIOS") || module.equals("ROLES"))) {
            if ("GERENTE".equalsIgnoreCase(roleName) || 
                "SISTEMAS".equalsIgnoreCase(roleName) || 
                "ADMIN".equalsIgnoreCase(roleName) ||
                "ROLE_ADMIN".equalsIgnoreCase(roleName) ||
                "ADMINISTRAR".equalsIgnoreCase(roleName)) {
                hasAccess = true;
                log.info("Acceso concedido implícitamente a {} por ser rol de alto nivel", roleName);
            }
        }

        log.info("Rol {} — módulo '{}' acción '{}': acceso={}", roleName, module, action, hasAccess);

        if (!hasAccess) {
            throw new UnauthorizedAccessException(
                "No tienes permisos para realizar esta acción en el módulo: " + module
            );
        }

        return true;
    }

    /**
     * Retorna el nombre del rol del usuario autenticado, o null si no hay sesión.
     */
    public String getCurrentUserRoleName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        if (user == null || user.getRole() == null) {
            return null;
        }

        return user.getRole().getName();
    }

    /**
     * Verifica si el usuario actual tiene rol de administrador total.
     * Se considera admin si tiene el permiso comodín ALL_MODULES para todas las acciones.
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        if (user == null || user.getRole() == null) {
            return false;
        }

        Set<String> permissions = rolePermissionRepository.findPermissionNamesByRoleId(user.getRole().getId());

        // Si tiene ALL_MODULES:DELETE puede hacer de todo → es admin completo
        return permissions.contains("ALL_MODULES:DELETE");
    }
}
