package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.AuthResponseDTO;
import com.everywhere.backend.model.dto.LoginDTO;
import com.everywhere.backend.service.UserService;
import com.everywhere.backend.utils.AuthTestData;
import com.everywhere.backend.utils.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest extends BaseControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        super.setupMockMvc(authController);
    }

    @Test
    void should_Return200AndToken_When_ValidLogin() throws Exception {
        // Arrange
        LoginDTO loginRequest = AuthTestData.createValidLoginDTO();
        AuthResponseDTO authResponse = AuthTestData.createAuthResponseDTO();
        
        when(userService.login(any(LoginDTO.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(authResponse.getName()))
                .andExpect(jsonPath("$.token").value(authResponse.getToken()))
                .andExpect(jsonPath("$.role").value(authResponse.getRole()));
    }

    @Test
    void should_Return401_When_InvalidCredentials() throws Exception {
        // Arrange
        LoginDTO loginRequest = AuthTestData.createValidLoginDTO();
        loginRequest.setPassword("wrong-password");
        
        when(userService.login(any(LoginDTO.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(loginRequest)))
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }
}
