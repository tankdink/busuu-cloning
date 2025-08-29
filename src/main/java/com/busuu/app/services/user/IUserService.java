package com.busuu.app.services.user;

import com.busuu.app.dtos.requests.user.UserActionPasswordDTO;
import com.busuu.app.dtos.requests.user.UserDTO;
import com.busuu.app.dtos.requests.user.UserLoginDTO;
import com.busuu.app.dtos.requests.user.UserUpdateDTO;
import com.busuu.app.dtos.responses.UserResponse;
import com.busuu.app.entities.User;
import com.busuu.app.exceptions.DataNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserService {

    UserResponse register (String requestId, UserDTO userDTO) throws Exception;

    String login (String requestId, String email, String password) throws Exception;

    boolean emailUnique (String requestId, String email);

    User getUserDetailsFromToken (String requestId, String token) throws Exception;

    User getUserDetailsFromRefreshToken (String requestId, String refreshToken) throws Exception;

    UserResponse updateUser (String requestId, UserUpdateDTO userUpdateDTO) throws Exception;

    Page<UserResponse> getUsersByRole (String requestId, String roleName, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, String isActive);

    UserResponse getUserById (String requestId, String userId);

    int generateOTP(String requestId, String email) throws DataNotFoundException;

    int activeAccount(String requestId, String email, String activeCode) throws DataNotFoundException;

    boolean changePassword (String requestId, UserActionPasswordDTO userActionPasswordDTO) throws Exception;

    boolean checkOTP (String requestId, String email, String OTP) throws DataNotFoundException;

    User blockOrEnable(String requestId, String userId) throws DataNotFoundException;

    String loginSocial(UserLoginDTO userLoginDTO) throws Exception;
}
