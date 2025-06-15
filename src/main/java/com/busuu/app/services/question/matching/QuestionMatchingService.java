package com.busuu.app.services.question.matching;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.questions.QuestionDTO;
import com.busuu.app.dtos.requests.questions.matching.QuestionMatchingDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.question.matching.MatchingPairResponse;
import com.busuu.app.dtos.responses.question.matching.QuestionMatchingResponse;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.questions.matching.MatchingPair;
import com.busuu.app.entities.questions.matching.QuestionMatching;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.grammar.GrammarSectionRepository;
import com.busuu.app.repositories.LessonRepository;
import com.busuu.app.repositories.questions.matching.MatchingPairRepository;
import com.busuu.app.repositories.questions.matching.QuestionMatchingRepository;
import com.busuu.app.services.cloudinary.IUploadCloudinaryService;
import com.busuu.app.utils.UploadCloudinaryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionMatchingService implements IQuestionMatchingService {
    private final QuestionMatchingRepository questionMatchingRepository;
    private final MatchingPairRepository matchingPairRepository;
    private final LessonRepository lessonRepository;
    private final GrammarSectionRepository grammarSectionRepository;
    private final IUploadCloudinaryService uploadCloudinaryService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public QuestionMatchingResponse insertQuestion(String requestId, QuestionMatchingDTO questionDTO) {
        try {
            Lesson existingLesson = questionDTO.getLessonId() != null ?
                    lessonRepository.findById(questionDTO.getLessonId()).orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + questionDTO.getLessonId()))
                    : null;
            GrammarSection existingGrammarSection = questionDTO.getGrammarSectionId() != null ?
                    grammarSectionRepository.findById(questionDTO.getGrammarSectionId()).orElseThrow(() -> new DataNotFoundException("Cannot find Grammar section with ID = " + questionDTO.getGrammarSectionId()))
                    : null;

            QuestionMatching question = modelMapper.map(questionDTO, QuestionMatching.class);
            question.setId(UUID.randomUUID().toString());
            question.setLesson(existingLesson);
            question.setGrammarSection(existingGrammarSection);

            if (questionDTO.getImage() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionDTO.getImage(), "image");
                question.setImageUrl(cloudinaryResponse.getUrl());
                question.setImageName(cloudinaryResponse.getPublicId());
            }

            if (questionDTO.getVideo() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionDTO.getVideo(), "video");
                question.setVideoUrl(cloudinaryResponse.getUrl());
                question.setVideoName(cloudinaryResponse.getPublicId());
            }

            if (questionDTO.getAudio() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionDTO.getAudio(), "audio");
                question.setAudioUrl(cloudinaryResponse.getUrl());
                question.setAudioName(cloudinaryResponse.getPublicId());
            }

            // Handle ans to save
            QuestionMatching finalQuestion = question;
            question.getPairs().forEach(
                    pair -> {
                        pair.setId(UUID.randomUUID().toString());
                        pair.setQuestionMatching(finalQuestion);
                    });

            question = questionMatchingRepository.save(question);

            QuestionMatchingResponse response = modelMapper.map(question, QuestionMatchingResponse.class);
            response.setLessonId(question.getLesson() != null ? question.getLesson().getId() : null);
            response.setGrammarSectionId(question.getGrammarSection() != null ? question.getGrammarSection().getId() : null);

            // Handle ans res
            response.setPairs(question.getPairs().stream().map(
                    pair -> modelMapper.map(pair, MatchingPairResponse.class)
            ).toList());

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create question matching, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_QUESTION, requestId);
        }
    }

    @Override
    public QuestionMatchingResponse getQuestion(String requestId, String questionId) {
        try {
            QuestionMatching existingQuestion = questionMatchingRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));

            QuestionMatchingResponse response = modelMapper.map(existingQuestion, QuestionMatchingResponse.class);
            response.setLessonId(existingQuestion.getLesson() != null ? existingQuestion.getLesson().getId() : null);
            response.setGrammarSectionId(existingQuestion.getGrammarSection() != null ? existingQuestion.getGrammarSection().getId() : null);
            response.setPairs(existingQuestion.getPairs().stream().map(
                    matchingPair -> modelMapper.map(matchingPair, MatchingPairResponse.class)
                        ).toList()
            );

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to get question matching, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public QuestionMatchingResponse updateQuestion(String requestId, String questionId, QuestionMatchingDTO questionDTO) {
        try {

            QuestionMatching existingQuestion = questionMatchingRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));

            // Handle Lesson
            if (questionDTO.getLessonId() == null) {
                if (existingQuestion.getLesson() != null) {
                    existingQuestion.setLesson(null);
                }
            } else {
                if (existingQuestion.getLesson() == null || !existingQuestion.getLesson().getId().equals(questionDTO.getLessonId())) {
                    Lesson newLesson = lessonRepository.findById(questionDTO.getLessonId())
                            .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + questionDTO.getLessonId()));
                    existingQuestion.setLesson(newLesson);
                }
            }

            // Handle Grammar Section
            if (questionDTO.getGrammarSectionId() == null) {
                if (existingQuestion.getGrammarSection() != null) {
                    existingQuestion.setGrammarSection(null);
                }
            } else {
                if (existingQuestion.getGrammarSection() == null || !existingQuestion.getGrammarSection().getId().equals(questionDTO.getGrammarSectionId())) {
                    GrammarSection newGrammarSection = grammarSectionRepository.findById(questionDTO.getGrammarSectionId())
                            .orElseThrow(() -> new DataNotFoundException("Cannot find Grammar section with ID = " + questionDTO.getGrammarSectionId()));
                    existingQuestion.setGrammarSection(newGrammarSection);
                }
            }

            modelMapper.map(questionDTO, existingQuestion);

            if (questionDTO.getImage() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionDTO.getImage(), "image");
                existingQuestion.setImageUrl(cloudinaryResponse.getUrl());
                existingQuestion.setImageName(cloudinaryResponse.getPublicId());
            }

            if (questionDTO.getVideo() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionDTO.getVideo(), "video");
                existingQuestion.setVideoUrl(cloudinaryResponse.getUrl());
                existingQuestion.setVideoName(cloudinaryResponse.getPublicId());
            }

            if (questionDTO.getAudio() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionDTO.getAudio(), "audio");
                existingQuestion.setAudioUrl(cloudinaryResponse.getUrl());
                existingQuestion.setAudioName(cloudinaryResponse.getPublicId());
            }

            // Handle ans
            existingQuestion.getPairs().clear();

            QuestionMatching finalExistingQuestion = existingQuestion;
            questionDTO.getPairs().forEach(pairDto -> {
                MatchingPair newPair = modelMapper.map(pairDto, MatchingPair.class);
                newPair.setId(UUID.randomUUID().toString());
                newPair.setQuestionMatching(finalExistingQuestion);
                finalExistingQuestion.getPairs().add(newPair);
            });

            existingQuestion = questionMatchingRepository.save(existingQuestion);


            QuestionMatchingResponse response = modelMapper.map(existingQuestion, QuestionMatchingResponse.class);
            response.setLessonId(existingQuestion.getLesson() != null ? existingQuestion.getLesson().getId() : null);
            response.setGrammarSectionId(existingQuestion.getGrammarSection() != null ? existingQuestion.getGrammarSection().getId() : null);
            response.setPairs(existingQuestion.getPairs().stream().map(
                    matchingPair -> modelMapper.map(matchingPair, MatchingPairResponse.class))
                    .toList()
            );
            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to update question matching, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteQuestion(String requestId, String questionId) {
        try {
            questionMatchingRepository.deleteById(questionId);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to delete question matching, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_QUESTION, requestId);
        }
    }

    private CloudinaryResponse uploadMedia(MultipartFile file, String type) throws Exception {
        UploadCloudinaryUtil.assertAllowed(file, type);
        String fileName = UploadCloudinaryUtil.getFileName(file.getOriginalFilename());
        CloudinaryResponse response = uploadCloudinaryService.uploadFile(file, fileName, type);
        return response;
    }
}
