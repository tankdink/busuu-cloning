package com.busuu.app.services.user;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.user.UserDTO;
import com.busuu.app.dtos.requests.user.UserUpdateDTO;
import com.busuu.app.dtos.responses.UserResponse;
import com.busuu.app.entities.User;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserResponse insertUser(String requestId, UserDTO userDTO) throws Exception {
        try {
            return null;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to create user, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.NOT_FOUND,
                    Constants.ERROR_CODE.ERR_CREATE_USER, requestId);
        }
    }

    @Override
    public String login(String email, String password) throws Exception {
        return null;
    }

    @Override
    public boolean emailUnique(String email) {
        return false;
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        return null;
    }

    @Override
    public UserResponse updateUser(String userId, UserUpdateDTO userUpdateDTO) throws Exception {
        return null;
    }
}
