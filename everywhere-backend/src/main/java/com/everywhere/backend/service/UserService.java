package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface UserService {
    AuthResponseDTO login(LoginDTO loginDTO);
    Integer getAuthenticatedUserIdFromJWT();

    User getUserbyId(Integer userId);
    List<User> getAllUsers();
    UserBasicDTO getUserBasicInfo(Integer userId);
}