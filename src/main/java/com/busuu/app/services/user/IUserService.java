package com.busuu.app.services.user;

import com.busuu.app.dtos.requests.user.UserDTO;
import com.busuu.app.dtos.requests.user.UserUpdateDTO;
import com.busuu.app.dtos.responses.UserResponse;
import com.busuu.app.entities.User;

public interface IUserService {

    UserResponse register (String requestId, UserDTO userDTO) throws Exception;

    String login (String requestId, String email, String password) throws Exception;

    boolean emailUnique (String requestId, String email);

    User getUserDetailsFromToken (String requestId, String token) throws Exception;

    UserResponse updateUser (String requestId, String userId, UserUpdateDTO userUpdateDTO) throws Exception;
}
