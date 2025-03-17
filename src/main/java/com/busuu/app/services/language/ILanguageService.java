package com.busuu.app.services.language;

import com.busuu.app.dtos.requests.language.LanguageDTO;
import com.busuu.app.dtos.responses.LanguageResponse;
import com.busuu.app.entities.Language;

import java.util.List;

public interface ILanguageService
{
    LanguageResponse insertLanguage(String requestId, LanguageDTO languageDTO);
    List<LanguageResponse> getLanguages(String requestId);
    LanguageResponse getLanguage(String requestId, String languageID);
    LanguageResponse updateLanguage(String requestId, String languageId, LanguageDTO infoUpdateLanguage);
    void deleteLanguage(String requestId, String languageId);
}
