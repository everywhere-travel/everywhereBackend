package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.AuthResponseDTO;
import com.everywhere.backend.model.dto.LoginDTO;
import com.everywhere.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return new ResponseEntity<>(userService.login(loginDTO), HttpStatus.OK);
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<String> sendVerificationCode(@Valid @RequestBody com.everywhere.backend.model.dto.VerificationRequestDTO req) {
        userService.sendVerificationCode(req.getEmail());
        return ResponseEntity.ok("Código de verificación enviado.");
    }

    @PostMapping("/verify-password-change")
    public ResponseEntity<String> verifyPasswordChange(@Valid @RequestBody com.everywhere.backend.model.dto.PasswordChangeRequestDTO req) {
        userService.changePasswordWithCode(req.getEmail(), req.getCode(), req.getNewPassword());
        return ResponseEntity.ok("Contraseña cambiada exitosamente.");
    }
}
