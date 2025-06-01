package com.busuu.app.services.user;


import com.busuu.app.components.JwtTokenUtil;
import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.user.UserDTO;
import com.busuu.app.dtos.requests.user.UserUpdateDTO;
import com.busuu.app.dtos.responses.UserResponse;
import com.busuu.app.entities.Role;
import com.busuu.app.entities.User;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.PermissionDenyException;
import com.busuu.app.repositories.RoleRepository;
import com.busuu.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserResponse register (String requestId, UserDTO userDTO) throws Exception {
        try {
            // Check email
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new DataIntegrityViolationException("Email already exists");
            }
            List<Role> roles = userDTO.getRoleIds().stream()
                    .map(roleId -> {
                        try {
                            Role role = roleRepository.findById(roleId)
                                    .orElseThrow(() -> new DataNotFoundException("Cannot find Role with ID = " + roleId));
                            if (role.getName().equalsIgnoreCase("ADMIN")) {
                                throw new PermissionDenyException("You can not register a admin account");
                            }
                            return role;
                        } catch (DataNotFoundException | PermissionDenyException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            User newUser = modelMapper.map(userDTO, User.class);
            newUser.setId(UUID.randomUUID().toString());

            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);

            newUser.setRoles(roles);

            // Active code to active account
            newUser.setActiveCode(UUID.randomUUID().toString());
            newUser.setActive(true);

            // Save new user to db
            userRepository.save(newUser);

            // Send email active account here!

            return modelMapper.map(newUser, UserResponse.class);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to register user, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_REGISTER_USER, requestId);
        }
    }

    @Override
    public String login(String requestId, String email, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new DataNotFoundException("Invalid email or password");
        }
        User existingUser= optionalUser.get();

        // Check password
        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new BadCredentialsException("Wrong email or password");
        }

        // Authenticate with Java Spring Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email, password,
                existingUser.getAuthorities()
        );
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public boolean emailUnique(String requestId, String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getUserDetailsFromToken(String requestId, String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        String email = jwtTokenUtil.extractEmail(token);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        else {
            throw new Exception("User not found");
        }
    }

    @Override
    public UserResponse updateUser(String requestId, String userId, UserUpdateDTO userUpdateDTO) throws Exception {
        return null;
    }

    @Override
    public List<UserResponse> getUsersByRole(String requestId, String roleName) {
        try {
            return userRepository.findUsersByRoleName(roleName).stream().map(
                    user -> modelMapper.map(user, UserResponse.class)
            ).toList();
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get users by role, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_USER, requestId);
        }
    }
}
