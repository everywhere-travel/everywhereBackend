package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.UpdateUserNameDTO;
import com.everywhere.backend.model.dto.UserProfileDTO;
import com.everywhere.backend.security.UserPrincipal;
import com.everywhere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import com.everywhere.backend.model.dto.UserRequestDTO;
import com.everywhere.backend.model.dto.UserResponseDTO;
import com.everywhere.backend.mapper.UserMapper;
import com.everywhere.backend.security.RequirePermission;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile() {
        Integer userId = resolveCurrentUserId();
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileDTO> updateCurrentUserName(@RequestBody UpdateUserNameDTO request) {
        Integer userId = resolveCurrentUserId();
        String name = request != null ? request.getName() : null;
        return ResponseEntity.ok(userService.updateUserName(userId, name));
    }

    private Integer resolveCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return principal.getId();
    }

    @RequirePermission(module = "USUARIOS", permission = "READ")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers().stream()
                .map(userMapper::toUserResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @RequirePermission(module = "USUARIOS", permission = "CREATE")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.status(201).body(userService.createUser(request));
    }

    @RequirePermission(module = "USUARIOS", permission = "UPDATE")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @RequirePermission(module = "USUARIOS", permission = "DELETE")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @RequirePermission(module = "USUARIOS", permission = "UPDATE")
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<UserResponseDTO> toggleUserStatus(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }
}
