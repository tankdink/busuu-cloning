package com.busuu.app.services.user;


import com.busuu.app.components.JwtTokenUtil;
import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.user.UserActionPasswordDTO;
import com.busuu.app.dtos.requests.user.UserDTO;
import com.busuu.app.dtos.requests.user.UserUpdateDTO;
import com.busuu.app.dtos.responses.UserResponse;
import com.busuu.app.entities.Role;
import com.busuu.app.entities.Token;
import com.busuu.app.entities.User;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.PermissionDenyException;
import com.busuu.app.repositories.RoleRepository;
import com.busuu.app.repositories.TokenRepository;
import com.busuu.app.repositories.UserRepository;
import com.busuu.app.services.email.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
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
    private final TokenRepository tokenRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final ModelMapper modelMapper;
    private final IEmailService emailService;

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
            newUser.setActive(false);

            // Save new user to db
            userRepository.save(newUser);

            // Send email the active account here!
            emailService.sendEmailActive(newUser.getEmail(), newUser.getActiveCode());
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
        try {
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
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get detail user from token, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_USER, requestId);
        }
    }

    @Override
    public User getUserDetailsFromRefreshToken(String requestId, String refreshToken) throws Exception {
        try {
            Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
            return getUserDetailsFromToken(requestId, existingToken.getToken());
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get detail user from fresh token, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_USER, requestId);
        }
    }

    @Override
    public UserResponse updateUser(String requestId, String userId, UserUpdateDTO userUpdateDTO) {
        return null;
    }

    @Override
    @Transactional
    public boolean changePassword (String requestId, UserActionPasswordDTO userActionPasswordDTO) {
        try {
            User user = userRepository.findByEmail(userActionPasswordDTO.getEmail())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with Email = " + userActionPasswordDTO.getEmail()));
            if (passwordEncoder.matches(userActionPasswordDTO.getOldPassword(), user.getPassword())) {
                if (userActionPasswordDTO.getPassword().equals(userActionPasswordDTO.getRetypePassword())) {
                    String encodedNewPassword = passwordEncoder.encode(userActionPasswordDTO.getPassword());
                    user.setPassword(encodedNewPassword);
                    userRepository.save(user);
                    List<Token> tokens = tokenRepository.findByUser(user);
                    for (Token tokenItem : tokens) {
                        tokenRepository.delete(tokenItem);
                    }
                    emailService.sendEmailChangedPassword(user.getEmail());
                    return true;
                }
                else {
                    return false;
                }
            }
            return false;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to change password, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CHANGE_PASSWORD, requestId);
        }
    }

    @Override
    public Page<UserResponse> getUsersByRole(String requestId, String roleName, int page, int size, String sortBy, String sortDirection) {
        try {

            //Pageable
            Sort sort = Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection)));
            Pageable pageable = PageRequest.of(page, size, sort);

            return userRepository.findUsersByRoleName(roleName, pageable).map(
                    user -> modelMapper.map(user, UserResponse.class)
            );

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get users by role, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_USER, requestId);
        }
    }

    @Override
    public UserResponse getUserById(String requestId, String userId) {
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + userId));
            return modelMapper.map(existingUser, UserResponse.class);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get user by id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_USER, requestId);
        }
    }

    @Override
    @Transactional
    public int activeAccount(String requestId, String email, String activeCode) throws DataNotFoundException {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with Email = " + email));

            if (user.isActive()) {
                return 1;
            }

            if (activeCode.equals(user.getActiveCode())) {
                user.setActive(true);
                userRepository.save(user);
                return 2;
            }
            return 0;
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to active account, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_ACTIVE_ACCOUNT, requestId);
        }
    }


    // Handle -> Use redis
    @Override
    @Transactional
    public int generateOTP(String requestId, String email) throws DataNotFoundException {
        try {
            User existingUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with Email = " + email));
            if (existingUser.getOtp() != null) {
                existingUser.setOtp(null);
                userRepository.save(existingUser);
                return 1;
            }
            else {
                SecureRandom random = new SecureRandom();
                int otp = 100000 + random.nextInt(900000);
                existingUser.setOtp(String.valueOf(otp));
                emailService.sendEmailOtp(existingUser.getEmail(), String.valueOf(otp));
                userRepository.save(existingUser);
                return 2;
            }
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to generate otp, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GENERATE_OTP, requestId);
        }
    }

    @Override
    @Transactional
    public boolean checkOTP (String requestId, String email, String OTP) throws DataNotFoundException {
        try {
            User existingUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with Email = " + email));
            if (existingUser.getOtp().equals(OTP)) {
                existingUser.setOtp(null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to check otp, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CHECK_OTP, requestId);
        }
    }

    @Override
    @Transactional
    public User blockOrEnable(String requestId, String userId) throws DataNotFoundException {
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + userId));
            existingUser.setActive(!existingUser.isActive());
            return userRepository.save(existingUser);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to block or enable user, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CHECK_OTP, requestId);
        }
    }
}
