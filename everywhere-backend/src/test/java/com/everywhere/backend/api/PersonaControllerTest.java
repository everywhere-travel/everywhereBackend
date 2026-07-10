package com.everywhere.backend.api;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.dto.PersonaRequestDTO;
import com.everywhere.backend.model.dto.PersonaResponseDTO;
import com.everywhere.backend.model.dto.PersonaDisplayDto;
import com.everywhere.backend.service.PersonaService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PersonaControllerTest extends BaseControllerTest {

    @Mock
    private PersonaService personaService;

    @InjectMocks
    private PersonaController personaController;

    @BeforeEach
    void setUp() {
        setupMockMvc(personaController);
    }

    @Test
    void should_Return200AndList_When_GetAllPersonas() throws Exception {
        List<PersonaResponseDTO> list = Collections.singletonList(PersonaTestData.getValidResponse());
        when(personaService.findAll()).thenReturn(list);

        mockMvc.perform(get("/personas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].direccion").value("Av. Las Flores 123"));
    }

    @Test
    void should_Return200AndPersona_When_GetPersonaById() throws Exception {
        PersonaResponseDTO responseDTO = PersonaTestData.getValidResponse();
        when(personaService.findById(1)).thenReturn(responseDTO);

        mockMvc.perform(get("/personas/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.direccion").value("Av. Las Flores 123"));
    }

    @Test
    void should_Return404_When_GetPersonaById_NotExists() throws Exception {
        when(personaService.findById(99)).thenThrow(new ResourceNotFoundException("Persona no encontrada"));

        mockMvc.perform(get("/personas/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Persona no encontrada"));
    }

    @Test
    void should_Return201AndPersona_When_CreatePersona() throws Exception {
        PersonaRequestDTO request = PersonaTestData.getValidRequest();
        PersonaResponseDTO response = PersonaTestData.getValidResponse();

        when(personaService.save(any(PersonaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.direccion").value("Av. Las Flores 123"));
    }

    @Test
    void should_Return200AndPersona_When_PatchPersona() throws Exception {
        PersonaRequestDTO request = PersonaTestData.getValidRequest();
        PersonaResponseDTO response = PersonaTestData.getValidResponse();

        when(personaService.patch(eq(1), any(PersonaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/personas/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void should_Return204_When_DeletePersona() throws Exception {
        doNothing().when(personaService).deleteById(1);

        mockMvc.perform(delete("/personas/{id}", 1))
                .andExpect(status().isNoContent());

        verify(personaService, times(1)).deleteById(1);
    }

    @Test
    void should_Return200AndList_When_GetPersonasByEmail() throws Exception {
        List<PersonaResponseDTO> list = Collections.singletonList(PersonaTestData.getValidResponse());
        when(personaService.findByEmail("test@test.com")).thenReturn(list);

        mockMvc.perform(get("/personas/email").param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void should_Return200AndList_When_GetPersonasByTelefono() throws Exception {
        List<PersonaResponseDTO> list = Collections.singletonList(PersonaTestData.getValidResponse());
        when(personaService.findByTelefono("999888777")).thenReturn(list);

        mockMvc.perform(get("/personas/telefono").param("telefono", "999888777"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void should_Return200AndStats_When_GetPersonaStats() throws Exception {
        Map<String, Long> stats = new HashMap<>();
        stats.put("TOTAL", 100L);
        stats.put("NATURAL", 60L);
        stats.put("JURIDICA", 40L);

        when(personaService.getPersonaStats()).thenReturn(stats);

        mockMvc.perform(get("/personas/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.TOTAL").value(100))
                .andExpect(jsonPath("$.NATURAL").value(60));
    }

    @Test
    void should_Return200AndDisplay_When_FindPersonaNaturalOrJuridicaById() throws Exception {
        PersonaDisplayDto display = PersonaTestData.getValidDisplay();
        when(personaService.findPersonaNaturalOrJuridicaById(1)).thenReturn(display);

        mockMvc.perform(get("/personas/{personaId}/NaturalOrJuridica", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("NATURAL"))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"));
    }

    @Test
    void should_Return200AndDisplay_When_FindPersonaNaturalOrJuridicaByIdDropdown() throws Exception {
        PersonaDisplayDto display = PersonaTestData.getValidDisplay();
        when(personaService.findPersonaNaturalOrJuridicaById(1)).thenReturn(display);

        mockMvc.perform(get("/personas/dropdown/{personaId}/NaturalOrJuridica", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("NATURAL"));
    }
}
