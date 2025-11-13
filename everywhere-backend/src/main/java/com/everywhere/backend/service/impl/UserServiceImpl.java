package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.UserNotFoundException;
import io.jsonwebtoken.Claims;
import com.everywhere.backend.mapper.UserMapper;
import com.everywhere.backend.model.dto.*;

import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.repository.UserRepository;
import com.everywhere.backend.security.TokenProvider;
import com.everywhere.backend.security.UserPrincipal;
import com.everywhere.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Override
    public AuthResponseDTO login(LoginDTO loginDTO) {
        // Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginDTO.getEmail(), loginDTO.getPassword()));

        // Obtener datos del usuario autenticado
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser(); // Generar token JWT
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
}