package com.busuu.app.services.userWord;

import com.busuu.app.dtos.requests.word.ReviewResultRequest;
import com.busuu.app.dtos.responses.UserWordResponse;
import com.busuu.app.dtos.responses.WordFilterResponse;
import com.busuu.app.dtos.responses.WordResponse;
import com.busuu.app.entities.enums.StrengthLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserWordService {

    Page<UserWordResponse> getWordsByUser (String requestId, Pageable pageable, StrengthLevel strengthLevel, Boolean isFavorite);

    void favoriteWord (String requestId, String wordId);

    void deleteWord (String requestId, String wordId);

    List<WordFilterResponse> listFilter (String requestId);

    void processReviewResults(List<ReviewResultRequest> results);

    List<WordResponse> getReviewWords(String requestId, String type, StrengthLevel strengthLevel);
}