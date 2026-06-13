package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer> {

    List<RolePermission> findByRoleId(Integer roleId);

    boolean existsByRoleIdAndPermissionId(Integer roleId, Integer permissionId);

    void deleteByRoleIdAndPermissionId(Integer roleId, Integer permissionId);

    // Obtiene solo los nombres de permisos de un rol (optimizado, sin cargar objetos completos)
    @Query("SELECT rp.permission.name FROM RolePermission rp WHERE rp.role.id = :roleId")
    Set<String> findPermissionNamesByRoleId(@Param("roleId") Integer roleId);
}
