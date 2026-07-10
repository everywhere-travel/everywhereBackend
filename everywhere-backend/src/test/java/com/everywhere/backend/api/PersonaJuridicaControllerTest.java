package com.everywhere.backend.api;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.dto.PersonaJuridicaRequestDTO;
import com.everywhere.backend.model.dto.PersonaJuridicaResponseDTO;
import com.everywhere.backend.service.PersonaJuridicaService;
import com.everywhere.backend.utils.JsonUtil;
import com.everywhere.backend.utils.PersonaTestData;
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
class PersonaJuridicaControllerTest extends BaseControllerTest {

    @Mock
    private PersonaJuridicaService personaJuridicaService;

    @InjectMocks
    private PersonaJuridicaController personaJuridicaController;

    @BeforeEach
    void setUp() {
        setupMockMvc(personaJuridicaController);
    }

    @Test
    void should_Return200AndList_When_GetAllPersonasJuridicas() throws Exception {
        List<PersonaJuridicaResponseDTO> list = Collections.singletonList(PersonaTestData.getValidJuridicaResponse());
        when(personaJuridicaService.findAll()).thenReturn(list);

        mockMvc.perform(get("/personas-juridicas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].ruc").value("20123456789"));
    }

    @Test
    void should_Return200AndList_When_GetDropdown() throws Exception {
        List<PersonaJuridicaResponseDTO> list = Collections.singletonList(PersonaTestData.getValidJuridicaResponse());
        when(personaJuridicaService.getDropdown("Empresa")).thenReturn(list);

        mockMvc.perform(get("/personas-juridicas/dropdown").param("search", "Empresa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void should_Return200AndList_When_GetByRuc() throws Exception {
        List<PersonaJuridicaResponseDTO> list = Collections.singletonList(PersonaTestData.getValidJuridicaResponse());
        when(personaJuridicaService.findByRuc("20123456789")).thenReturn(list);

        mockMvc.perform(get("/personas-juridicas/ruc").param("ruc", "20123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void should_Return200AndList_When_GetByRazonSocial() throws Exception {
        List<PersonaJuridicaResponseDTO> list = Collections.singletonList(PersonaTestData.getValidJuridicaResponse());
        when(personaJuridicaService.findByRazonSocial("Empresa")).thenReturn(list);

        mockMvc.perform(get("/personas-juridicas/razSocial").param("razonSocial", "Empresa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void should_Return200AndPersona_When_GetPersonaJuridicaById() throws Exception {
        PersonaJuridicaResponseDTO responseDTO = PersonaTestData.getValidJuridicaResponse();
        when(personaJuridicaService.findById(1)).thenReturn(responseDTO);

        mockMvc.perform(get("/personas-juridicas/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ruc").value("20123456789"));
    }

    @Test
    void should_Return404_When_GetPersonaJuridicaById_NotExists() throws Exception {
        when(personaJuridicaService.findById(99)).thenThrow(new ResourceNotFoundException("Persona no encontrada"));

        mockMvc.perform(get("/personas-juridicas/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }

    @Test
    void should_Return201AndPersona_When_CreatePersonaJuridica() throws Exception {
        PersonaJuridicaRequestDTO request = PersonaTestData.getValidJuridicaRequest();
        PersonaJuridicaResponseDTO response = PersonaTestData.getValidJuridicaResponse();

        when(personaJuridicaService.save(any(PersonaJuridicaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/personas-juridicas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.razonSocial").value("Empresa SAC"));
    }

    @Test
    void should_Return200AndPersona_When_PatchPersonaJuridica() throws Exception {
        PersonaJuridicaRequestDTO request = PersonaTestData.getValidJuridicaRequest();
        PersonaJuridicaResponseDTO response = PersonaTestData.getValidJuridicaResponse();

        when(personaJuridicaService.patch(eq(1), any(PersonaJuridicaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/personas-juridicas/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void should_Return204_When_DeletePersonaJuridica() throws Exception {
        doNothing().when(personaJuridicaService).deleteById(1);

        mockMvc.perform(delete("/personas-juridicas/{id}", 1))
                .andExpect(status().isNoContent());

        verify(personaJuridicaService, times(1)).deleteById(1);
    }
}
