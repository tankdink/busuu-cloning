package com.busuu.app.services.question.matching;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.questions.matching.MatchingPairDTO;
import com.busuu.app.dtos.responses.question.matching.MatchingPairResponse;
import com.busuu.app.entities.questions.matching.MatchingPair;
import com.busuu.app.entities.questions.matching.QuestionMatching;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.MatchingPairRepository;
import com.busuu.app.repositories.QuestionMatchingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingPairService implements IMatchingPairService {
    private final MatchingPairRepository matchingPairRepository;
    private final QuestionMatchingRepository questionMatchingRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public MatchingPairResponse insertMatchingPair(String requestId, MatchingPairDTO matchingPairDTO) {
        try {
            QuestionMatching existingQuestion = questionMatchingRepository.findById(matchingPairDTO.getQuestionMatchingId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot not find Question with ID = " + matchingPairDTO.getQuestionMatchingId()));

            MatchingPair matchingPair = modelMapper.map(matchingPairDTO, MatchingPair.class);
            matchingPair.setQuestionMatching(existingQuestion);

            matchingPair = matchingPairRepository.save(matchingPair);

            MatchingPairResponse response = modelMapper.map(matchingPair, MatchingPairResponse.class);
            response.setQuestionId(matchingPair.getQuestionMatching().getId());

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create matching pair, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_MATCHING_PAIR, requestId);
        }
    }

    @Override
    public MatchingPairResponse getMatchingPair(String requestId, String matchingPairId) {
        try {
            MatchingPair existingMatchingPair = matchingPairRepository.findById(matchingPairId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Matching Pair with ID = " + matchingPairId));

            MatchingPairResponse response = modelMapper.map(existingMatchingPair, MatchingPairResponse.class);
            response.setQuestionId(existingMatchingPair.getQuestionMatching().getId());

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to get matching pair, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_MATCHING_PAIR, requestId);
        }
    }

    @Override
    public List<MatchingPairResponse> getByQuestionId(String requestId, String questionId) {
        try {
            QuestionMatching existingQuestionMatching = questionMatchingRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));
            List<MatchingPair> existingMatchingPair = matchingPairRepository.findByQuestionMatching(existingQuestionMatching);

            return existingMatchingPair.stream().map(
                    matchingPair -> {
                        MatchingPairResponse response = modelMapper.map(matchingPair, MatchingPairResponse.class);
                        response.setQuestionId(matchingPair.getQuestionMatching().getId());
                        return response;
                    }
            ).toList();
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to get matching pair, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_MATCHING_PAIR, requestId);
        }
    }

    @Override
    @Transactional
    public MatchingPairResponse updateMatchingPair(String requestId, String matchingPairId, MatchingPairDTO matchingPairDTO) {
        try {
            MatchingPair existingMatchingPair = matchingPairRepository.findById(matchingPairId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Matching Pair with ID = " + matchingPairId));

            if (!existingMatchingPair.getQuestionMatching().getId().equals(matchingPairDTO.getQuestionMatchingId())) {
                QuestionMatching existingQuestion = questionMatchingRepository.findById(matchingPairDTO.getQuestionMatchingId())
                        .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + matchingPairDTO.getQuestionMatchingId()));
                existingMatchingPair.setQuestionMatching(existingQuestion);
            }
            modelMapper.map(matchingPairDTO, existingMatchingPair);

            existingMatchingPair = matchingPairRepository.save(existingMatchingPair);

            MatchingPairResponse response = modelMapper.map(existingMatchingPair, MatchingPairResponse.class);
            response.setQuestionId(existingMatchingPair.getQuestionMatching().getId());

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to update matching pair, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_MATCHING_PAIR, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteMatchingPair(String requestId, String matchingPairId) {
        try {
            matchingPairRepository.deleteById(matchingPairId);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to delete matching pair, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_MATCHING_PAIR, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteByQuestionId(String requestId, String questionId) {
        try {
            QuestionMatching existingQuestionMatching = questionMatchingRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));
            matchingPairRepository.deleteByQuestionMatching(existingQuestionMatching);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to delete matching pair, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_MATCHING_PAIR, requestId);
        }
    }
}
