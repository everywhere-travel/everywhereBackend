package com.everywhere.backend.api;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.dto.RolePermissionRequestDTO;
import com.everywhere.backend.model.dto.RoleRequestDTO;
import com.everywhere.backend.model.dto.RoleResponseDTO;
import com.everywhere.backend.service.RoleService;
import com.everywhere.backend.utils.JsonUtil;
import com.everywhere.backend.utils.RoleTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest extends BaseControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setUp() {
        setupMockMvc(roleController);
    }

    @Test
    void should_Return200AndList_When_FindAll() throws Exception {
        List<RoleResponseDTO> list = Collections.singletonList(RoleTestData.getValidRoleResponse());
        when(roleService.findAll()).thenReturn(list);

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("ADMIN"));
    }

    @Test
    void should_Return200AndRole_When_FindById() throws Exception {
        RoleResponseDTO response = RoleTestData.getValidRoleResponse();
        when(roleService.findById(1)).thenReturn(response);

        mockMvc.perform(get("/roles/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    void should_Return404_When_FindById_NotExists() throws Exception {
        when(roleService.findById(99)).thenThrow(new ResourceNotFoundException("Rol no encontrado"));

        mockMvc.perform(get("/roles/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Rol no encontrado"));
    }

    @Test
    void should_Return201AndRole_When_Create() throws Exception {
        RoleRequestDTO request = RoleTestData.getValidRoleRequest();
        RoleResponseDTO response = RoleTestData.getValidRoleResponse();

        when(roleService.create(any(RoleRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    void should_Return200AndRole_When_Update() throws Exception {
        RoleRequestDTO request = RoleTestData.getValidRoleRequest();
        RoleResponseDTO response = RoleTestData.getValidRoleResponse();

        when(roleService.update(eq(1), any(RoleRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/roles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    void should_Return204_When_Delete() throws Exception {
        doNothing().when(roleService).delete(1);

        mockMvc.perform(delete("/roles/{id}", 1))
                .andExpect(status().isNoContent());

        verify(roleService, times(1)).delete(1);
    }

    @Test
    void should_Return200AndRole_When_AddPermission() throws Exception {
        RolePermissionRequestDTO request = RoleTestData.getValidRolePermissionRequest();
        RoleResponseDTO response = RoleTestData.getValidRoleResponse();

        when(roleService.addPermission(1, 1)).thenReturn(response);

        mockMvc.perform(post("/roles/{roleId}/permissions", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.permissions[0]").value("CLIENTES:READ"));
    }

    @Test
    void should_Return200AndRole_When_RemovePermission() throws Exception {
        RoleResponseDTO response = RoleTestData.getValidRoleResponse();
        when(roleService.removePermission(1, 1)).thenReturn(response);

        mockMvc.perform(delete("/roles/{roleId}/permissions/{permissionId}", 1, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
