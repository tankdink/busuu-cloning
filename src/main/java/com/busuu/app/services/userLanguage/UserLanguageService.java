package com.busuu.app.services.userLanguage;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.user.UserLanguageDTO;
import com.busuu.app.dtos.responses.UserLanguageResponse;
import com.busuu.app.entities.Language;
import com.busuu.app.entities.User;
import com.busuu.app.entities.UserLanguage;
import com.busuu.app.entities.enums.LearningStatus;
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
            User user = userRepository.findById(userLanguageDTO.getUserId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + userLanguageDTO.getUserId()));

            UserLanguage existing = userLanguageRepository.findByUserIdAndLanguageId(user.getId(), userLanguageDTO.getLanguageId());

            if (existing != null) {
                if (existing.getLearningStatus() == LearningStatus.IN_PROGRESS
                        && userLanguageDTO.getLearningStatus() == LearningStatus.NOT_STARTED) {
                    throw new ErrorHandleException("Cannot set learning status from IN_PROGRESS to NOT_STARTED",
                            HttpStatus.BAD_REQUEST, Constants.ERROR_CODE.ERR_UPSERT_USER_LANGUAGE, requestId);
                }

                if (Boolean.FALSE.equals(userLanguageDTO.getIsLearning())) {
                    modelMapper.map(userLanguageDTO, existing);

                    if (userLanguageRepository.findByUserIdAndIsLearning(user.getId(), true) == null) {
                        throw new ErrorHandleException("Must have at least one language being learned!",
                                HttpStatus.BAD_REQUEST, Constants.ERROR_CODE.ERR_UPSERT_USER_LANGUAGE, requestId);
                    }
                } else {
                    handleLearningFlag(user, true);
                    modelMapper.map(userLanguageDTO, existing);
                }

                existing = userLanguageRepository.save(existing);
                return buildResponse(existing, user);
            }

            Language lang = languageRepository.findById(userLanguageDTO.getLanguageId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Language with ID = " + userLanguageDTO.getLanguageId()));

            UserLanguage newUL = modelMapper.map(userLanguageDTO, UserLanguage.class);
            newUL.setId(UUID.randomUUID().toString());
            newUL.setLanguage(lang);
            newUL.setUser(user);

            if (Boolean.TRUE.equals(userLanguageDTO.getIsLearning())) {
                if (userLanguageDTO.getLearningStatus() != LearningStatus.IN_PROGRESS) {
                    throw new ErrorHandleException("Cannot set learning status different IN_PROGRESS when learning is true",
                            HttpStatus.BAD_REQUEST, Constants.ERROR_CODE.ERR_UPSERT_USER_LANGUAGE, requestId);
                }
                handleLearningFlag(user, true);
                newUL.setLearningStatus(LearningStatus.IN_PROGRESS);
            } else {
                ensureAtLeastOneLearning(user);
            }

            newUL = userLanguageRepository.save(newUL);
            return buildResponse(newUL, user);

        } catch (Exception e) {
            log.error("requestId={}, failed to upsert user language, err={}", requestId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPSERT_USER_LANGUAGE, requestId);
        }
    }


    private void handleLearningFlag(User user, boolean isLearning) throws Exception {
        if (isLearning) {
            userLanguageRepository.findByUserId(user.getId())
                    .forEach(ul -> {
                        ul.setIsLearning(false);
                        userLanguageRepository.save(ul);
                    });
        } else {
            ensureAtLeastOneLearning(user);
        }
    }

    private void ensureAtLeastOneLearning(User user) throws Exception {
        UserLanguage learning = userLanguageRepository.findByUserIdAndIsLearning(user.getId(), true);
        if (learning == null) {
            throw new Exception("Must have at least one language being learned!");
        }
    }

    private UserLanguageResponse buildResponse(UserLanguage ul, User user) {
        UserLanguageResponse res = modelMapper.map(ul, UserLanguageResponse.class);
        res.setLanguageId(ul.getLanguage().getId());
        res.setUserId(user.getId());
        return res;
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
