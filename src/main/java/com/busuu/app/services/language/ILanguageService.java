package com.busuu.app.services.language;

import com.busuu.app.dtos.requests.language.LanguageDTO;
import com.busuu.app.dtos.responses.LanguageResponse;
import com.busuu.app.entities.Language;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ILanguageService
{
    LanguageResponse insertLanguage(String requestId, LanguageDTO languageDTO);
    Page<LanguageResponse> getLanguages(String requestId, int page, int size, String sortBy, String sortDirection);
    LanguageResponse getLanguage(String requestId, String languageID);
    LanguageResponse updateLanguage(String requestId, String languageId, LanguageDTO infoUpdateLanguage);
    void deleteLanguage(String requestId, String languageId);
    List<LanguageResponse> getAllWithTotalUsersLearning (String requestId);
}
