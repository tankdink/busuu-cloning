package com.busuu.app.services.question.matching;

import com.busuu.app.dtos.requests.questions.matching.MatchingPairDTO;
import com.busuu.app.dtos.responses.question.matching.MatchingPairResponse;

import java.util.List;

public interface IMatchingPairService {

    MatchingPairResponse insertMatchingPair (String requestId, MatchingPairDTO matchingPairDTO);

    MatchingPairResponse getMatchingPair (String requestId, String matchingPairId);

    List<MatchingPairResponse> getByQuestionId (String requestId, String questionId);

    MatchingPairResponse updateMatchingPair (String requestId,String matchingPairId, MatchingPairDTO matchingPairDTO);

    void deleteMatchingPair (String requestId, String matchingPairId);

    void deleteByQuestionId (String requestId, String questionId);
}
