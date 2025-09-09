package com.busuu.app.services.userLanguage;

import com.busuu.app.dtos.requests.user.UserLanguageDTO;
import com.busuu.app.dtos.responses.UserLanguageResponse;

import java.util.List;

public interface IUserLanguageService {

    UserLanguageResponse upSertUserLanguage (String requestId, UserLanguageDTO userLanguageDTO);

    List<UserLanguageResponse> getUserLanguages (String requestId);

    UserLanguageResponse getLearningLanguage(String requestId, boolean isLearning);

}
