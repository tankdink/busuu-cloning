package com.busuu.app.services.user;

import com.busuu.app.dtos.requests.user.UserDTO;
import com.busuu.app.dtos.requests.user.UserUpdateDTO;
import com.busuu.app.dtos.responses.UserResponse;
import com.busuu.app.entities.User;

public interface IUserService {

    UserResponse insertUser (String requestId, UserDTO userDTO) throws Exception;

    String login (String email, String password) throws Exception;

    boolean emailUnique (String email);

    User getUserDetailsFromToken (String token) throws Exception;

    UserResponse updateUser (String userId, UserUpdateDTO userUpdateDTO) throws Exception;
}
