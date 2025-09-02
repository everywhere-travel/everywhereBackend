package com.everywhere.backend.model.enums;

import java.util.Set;

public enum Role {
    ADMIN(1, "ADMIN",
        Set.of("READ", "CREATE", "UPDATE", "DELETE"),
        Set.of("COTIZACIONES", "PERSONAS", "LIQUIDACIONES", "VIAJEROS", "SISTEMA", "CONTABILIDAD", "ADMINISTRACION", "VENTAS", "USUARIOS", "PRODUCTOS", "PROVEEDORES", "SUCURSALES")),

    VENTAS_ADMIN(2, "VENTAS_ADMIN",
        Set.of("READ", "CREATE", "UPDATE", "DELETE"),
        Set.of("COTIZACIONES", "PERSONAS", "VIAJEROS")),

    VENTAS_JUNIOR(3, "VENTAS_JUNIOR",
        Set.of("READ", "CREATE", "UPDATE"),
        Set.of("COTIZACIONES", "PERSONAS", "VIAJEROS")),

    ADMINISTRACION_ADMIN(4, "ADMINISTRACION_ADMIN",
        Set.of("READ", "UPDATE"),
        Set.of("COTIZACIONES", "PERSONAS", "VIAJEROS", "LIQUIDACIONES", "CONTABILIDAD", "PRODUCTOS", "PROVEEDORES", "SUCURSALES")),

    ADMINISTRACION_JUNIOR(5, "ADMINISTRACION_JUNIOR",
        Set.of("READ", "UPDATE"),
        Set.of("COTIZACIONES", "PERSONAS", "VIAJEROS", "CONTABILIDAD", "PRODUCTOS", "PROVEEDORES", "SUCURSALES")),

    SISTEMAS(6, "SISTEMAS",
        Set.of("READ", "CREATE", "UPDATE", "DELETE"),
        Set.of("COTIZACIONES", "PERSONAS", "LIQUIDACIONES", "VIAJEROS", "SISTEMA", "CONTABILIDAD", "ADMINISTRACION", "VENTAS", "USUARIOS", "PRODUCTOS", "PROVEEDORES", "SUCURSALES")),

    CONTABILIDAD_ADMIN(7, "CONTABILIDAD_ADMIN",
        Set.of("READ", "CREATE", "UPDATE"),
        Set.of("COTIZACIONES", "PERSONAS", "LIQUIDACIONES", "VIAJEROS", "CONTABILIDAD", "PRODUCTOS", "PROVEEDORES", "SUCURSALES")),

    CONTABILIDAD_JUNIOR(8, "CONTABILIDAD_JUNIOR",
        Set.of("READ", "CREATE", "UPDATE"),
        Set.of("COTIZACIONES", "PERSONAS", "LIQUIDACIONES", "VIAJEROS", "CONTABILIDAD", "PRODUCTOS", "PROVEEDORES", "SUCURSALES"));

    private final Integer id;
    private final String name;
    private final Set<String> permissions;
    private final Set<String> modules;

    Role(Integer id, String name, Set<String> permissions, Set<String> modules) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
        this.modules = modules;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public Set<String> getModules() {
        return modules;
    }

    public static Role fromId(Integer id) {
        for (Role role : values()) {
            if (role.getId().equals(id)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role not found for id: " + id);
    }

    public static Role fromName(String name) {
        for (Role role : values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role not found for name: " + name);
    }

    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }

    public boolean hasModuleAccess(String module) {
        return this.modules.contains(module);
    }

    public boolean canAccess(String module, String permission) {
        return hasModuleAccess(module) && hasPermission(permission);
    }
}
