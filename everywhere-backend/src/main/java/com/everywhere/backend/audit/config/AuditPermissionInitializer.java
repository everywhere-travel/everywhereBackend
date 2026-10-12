package com.everywhere.backend.audit.config;

import com.everywhere.backend.model.entity.Permission;
import com.everywhere.backend.model.entity.Role;
import com.everywhere.backend.model.entity.RolePermission;
import com.everywhere.backend.repository.PermissionRepository;
import com.everywhere.backend.repository.RolePermissionRepository;
import com.everywhere.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Inicializador que asegura la existencia del permiso 'AUDITORIA:READ' en la base de datos
 * y su asignación automática al rol 'ADMIN' para que aparezca en la matriz de roles y permisos.
 */
@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class AuditPermissionInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    private static final String PERMISSION_NAME = "AUDITORIA:READ";
    private static final String PERMISSION_DESC = "Permite visualizar el registro maestro de auditoría";

    @Override
    public void run(String... args) {
        try {
            // 1. Asegurar permiso
            Permission auditPerm = permissionRepository.findByName(PERMISSION_NAME)
                    .orElseGet(() -> {
                        Permission p = new Permission();
                        p.setName(PERMISSION_NAME);
                        p.setDescription(PERMISSION_DESC);
                        Permission saved = permissionRepository.save(p);
                        log.info("Permiso de auditoría creado exitosamente: {}", PERMISSION_NAME);
                        return saved;
                    });

            // 2. Asignar a rol ADMIN (o ROLE_ADMIN) si no lo tiene asignado
            assignPermissionToRoleIfPresent("ADMIN", auditPerm);
            assignPermissionToRoleIfPresent("ROLE_ADMIN", auditPerm);

        } catch (Exception e) {
            log.warn("No se pudo auto-inicializar permiso de auditoría: {}", e.getMessage());
        }
    }

    private void assignPermissionToRoleIfPresent(String roleName, Permission permission) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            if (!rolePermissionRepository.existsByRoleIdAndPermissionId(role.getId(), permission.getId())) {
                RolePermission rp = new RolePermission();
                rp.setRole(role);
                rp.setPermission(permission);
                rolePermissionRepository.save(rp);
                log.info("Permiso {} asignado automáticamente al rol {}", permission.getName(), roleName);
            }
        }
    }
}
