package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.UpdateUserNameDTO;
import com.everywhere.backend.model.dto.UserProfileDTO;
import com.everywhere.backend.security.UserPrincipal;
import com.everywhere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}
