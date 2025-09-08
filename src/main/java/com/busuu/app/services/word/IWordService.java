package com.busuu.app.services.word;

import com.busuu.app.dtos.requests.word.WordDTO;
import com.busuu.app.dtos.responses.WordResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IWordService {
    WordResponse createWord (String requestId, WordDTO wordDTO);

    WordResponse getWord (String requestId, String wordId, String languageCodeDes);

    Page<WordResponse> getWords (String requestId, Pageable pageable, String lessonId, String keyword);

    WordResponse updateWord (String requestId, String wordId, WordDTO wordDTO);

    void deleteWord (String requestId, String wordId);

    Page<WordResponse> getWordsByLesson (String requestId, String lessonId, Pageable pageable);
}
