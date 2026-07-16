package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.UserNotFoundException;
import io.jsonwebtoken.Claims;
import com.everywhere.backend.mapper.UserMapper;
import com.everywhere.backend.model.dto.*;

import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.model.entity.Role;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.RoleRepository;
import com.everywhere.backend.repository.SucursalRepository;
import com.everywhere.backend.repository.UserRepository;
import com.everywhere.backend.security.TokenProvider;
import com.everywhere.backend.security.UserPrincipal;
import com.everywhere.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import com.everywhere.backend.model.entity.VerificationToken;
import com.everywhere.backend.model.dto.EmailRequestDTO;
import com.everywhere.backend.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SucursalRepository sucursalRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final com.everywhere.backend.repository.VerificationTokenRepository verificationTokenRepository;
    private final com.everywhere.backend.service.EmailService emailService;

    @Override
    public AuthResponseDTO login(LoginDTO loginDTO) {
        // Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginDTO.getEmail(), loginDTO.getPassword()));

        // Obtener datos del usuario autenticado
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser(); 
        
        // Incrementar el contador de logins
        user.setLoginCount((user.getLoginCount() == null ? 0 : user.getLoginCount()) + 1);
        userRepository.save(user);

        // Generar token JWT
        String token = tokenProvider.createAccessToken(authentication); // Retornar respuesta con token
        return userMapper.toAuthResponseDTO(user, token);
    }

    @Override
    public Integer getAuthenticatedUserIdFromJWT() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String token = (String) authentication.getCredentials();

            Claims claims = tokenProvider.getJwtParser().parseClaimsJws(token).getBody();
            String email = claims.getSubject();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + email));

            return user.getId();
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserbyId(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserBasicDTO getUserBasicInfo(Integer userId) {
        User user = getUserbyId(userId);
        return userMapper.toUserBasicDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(Integer userId) {
        User user = getUserbyId(userId);
        return userMapper.toUserProfileDTO(user);
    }

    @Override
    @Transactional
    public UserProfileDTO updateUserName(Integer userId, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        User user = getUserbyId(userId);
        user.setNombre(name.trim());
        return userMapper.toUserProfileDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        User user = new User();
        user.setNombre(request.getNombre());
        user.setEmail(request.getEmail());
        user.setEstado(true);

        String rawPassword = request.getPassword() != null && !request.getPassword().isEmpty() ? request.getPassword() : "123456";
        user.setPassword(passwordEncoder.encode(rawPassword));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        user.setRole(role);

        if (request.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada"));
            user.setSucursal(sucursal);
        }

        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Integer userId, UserRequestDTO request) {
        User user = getUserbyId(userId);

        if (!user.getEmail().equals(request.getEmail()) && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está en uso por otro usuario");
        }

        user.setNombre(request.getNombre());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        user.setRole(role);

        if (request.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada"));
            user.setSucursal(sucursal);
        } else {
            user.setSucursal(null);
        }

        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDTO toggleUserStatus(Integer userId) {
        User user = getUserbyId(userId);
        user.setEstado(user.getEstado() == null ? true : !user.getEstado());
        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User user = getUserbyId(userId);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void resetLoginCount(Integer userId) {
        User user = getUserbyId(userId);
        user.setLoginCount(0);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void sendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con correo: " + email));

        // Borrar token anterior si existe
        verificationTokenRepository.deleteByUser(user);

        // Generar código de 6 dígitos
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000);
        String code = String.format("%06d", num);

        VerificationToken token = new VerificationToken();
        token.setToken(code);
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        
        verificationTokenRepository.save(token);

        // Enviar correo
        EmailRequestDTO emailReq = new EmailRequestDTO();
        emailReq.setTo(email);
        emailReq.setSubject("Código de Verificación - Everywhere");
        emailReq.setBody("Hola " + user.getNombre() + ",<br><br>" +
                "Tu código de verificación para cambiar tu contraseña es: <b>" + code + "</b><br><br>" +
                "Este código expirará en 15 minutos.");
        
        emailService.sendEmail(emailReq, null);
    }

    @Override
    @Transactional
    public void changePasswordWithCode(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con correo: " + email));

        VerificationToken token = verificationTokenRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("No hay código de verificación activo para este usuario."));

        if (!token.getToken().equals(code)) {
            throw new IllegalArgumentException("El código ingresado es incorrecto.");
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(token);
            throw new IllegalArgumentException("El código ha expirado. Solicite uno nuevo.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        verificationTokenRepository.delete(token);
    }
}