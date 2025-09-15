package com.busuu.app.services.userLanguage;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.user.UserLanguageDTO;
import com.busuu.app.dtos.responses.UserLanguageResponse;
import com.busuu.app.entities.Language;
import com.busuu.app.entities.User;
import com.busuu.app.entities.UserLanguage;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.LanguageRepository;
import com.busuu.app.repositories.UserLanguageRepository;
import com.busuu.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLanguageService implements IUserLanguageService {

    private final UserLanguageRepository userLanguageRepository;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserLanguageResponse upSertUserLanguage(String requestId, UserLanguageDTO userLanguageDTO) {
        try {
            User user = null;
            if (userLanguageDTO.getUserId() != null || !userLanguageDTO.getUserId().isEmpty()) {

                user = userRepository.findById(userLanguageDTO.getUserId())
                        .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + userLanguageDTO.getUserId()));
            } else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                user = (User) auth.getPrincipal();
            }
            UserLanguage existingUserLanguage = userLanguageRepository.findByUserIdAndLanguageId(user.getId(), userLanguageDTO.getLanguageId());

            if (existingUserLanguage != null) {
                modelMapper.map(userLanguageDTO, existingUserLanguage);

                existingUserLanguage = userLanguageRepository.save(existingUserLanguage);

                UserLanguageResponse res = modelMapper.map(existingUserLanguage, UserLanguageResponse.class);
                res.setLanguageId(existingUserLanguage.getLanguage().getId());
                res.setUserId(user.getId());

                return res;
            }

            Language existingLanguage = languageRepository.findById(userLanguageDTO.getLanguageId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Language with ID = " + userLanguageDTO.getLanguageId()));

            UserLanguage userLanguage = modelMapper.map(userLanguageDTO, UserLanguage.class);
            userLanguage.setId(UUID.randomUUID().toString());
            userLanguage.setLanguage(existingLanguage);
            userLanguage.setUser(user);

            userLanguage = userLanguageRepository.save(userLanguage);

            UserLanguageResponse res = modelMapper.map(userLanguage, UserLanguageResponse.class);
            res.setLanguageId(existingLanguage.getId());
            res.setUserId(user.getId());

            return res;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to upsert user language, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPSERT_USER_LANGUAGE, requestId);
        }
    }

    @Override
    public List<UserLanguageResponse> getUserLanguages(String requestId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            List<UserLanguage> userLanguages = userLanguageRepository.findByUserId(userId);

            return userLanguages.stream().map(
                    userLanguage ->  {
                        UserLanguageResponse res = modelMapper.map(userLanguage, UserLanguageResponse.class);
                        res.setLanguageId(userLanguage.getLanguage().getId());
                        res.setUserId(userLanguage.getUser().getId());

                        return res;
                    }
            ).toList();
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get user languages, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_USER_LANGUAGE, requestId);
        }
    }

    @Override
    public List<UserLanguageResponse> getUserLanguages(String requestId, String userId) {
        try {
            List<UserLanguage> userLanguages = userLanguageRepository.findByUserId(userId);

            return userLanguages.stream().map(
                    userLanguage ->  {
                        UserLanguageResponse res = modelMapper.map(userLanguage, UserLanguageResponse.class);
                        res.setLanguageId(userLanguage.getLanguage().getId());
                        res.setUserId(userLanguage.getUser().getId());

                        return res;
                    }
            ).toList();
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get user languages, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_USER_LANGUAGE, requestId);
        }
    }

    @Override
    public UserLanguageResponse getLearningLanguage(String requestId, boolean isLearning)
    {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            UserLanguage languageLearning = userLanguageRepository.findByUserIdAndIsLearning(userId, isLearning);

            UserLanguageResponse response = modelMapper.map(languageLearning, UserLanguageResponse.class);
            response.setLanguageId(languageLearning.getLanguage().getId());
            response.setUserId(userId);

            return response;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get user language, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_USER_LANGUAGE, requestId);
        }
    }


}
