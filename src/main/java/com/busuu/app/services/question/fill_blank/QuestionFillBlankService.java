package com.busuu.app.services.question.fill_blank;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.questions.QuestionFillBlankDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.question.QuestionFillBlankResponse;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.questions.QuestionFillBlank;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.grammar.GrammarSectionRepository;
import com.busuu.app.repositories.LessonRepository;
import com.busuu.app.repositories.questions.QuestionFillBlankRepository;
import com.busuu.app.services.cloudinary.IUploadCloudinaryService;
import com.busuu.app.utils.UploadCloudinaryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionFillBlankService implements IQuestionFillBlankService {
    private final QuestionFillBlankRepository questionFillBlankRepository;
    private final LessonRepository lessonRepository;
    private final GrammarSectionRepository grammarSectionRepository;
    private final IUploadCloudinaryService uploadCloudinaryService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public QuestionFillBlankResponse insertQuestion(String requestId, QuestionFillBlankDTO questionFillBlankDTO) {
        try {
            Lesson existingLesson = questionFillBlankDTO.getLessonId() != null ?
                    lessonRepository.findById(questionFillBlankDTO.getLessonId()).orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + questionFillBlankDTO.getLessonId()))
                    : null;
            GrammarSection existingGrammarSection = questionFillBlankDTO.getGrammarSectionId() != null ?
                    grammarSectionRepository.findById(questionFillBlankDTO.getGrammarSectionId()).orElseThrow(() -> new DataNotFoundException("Cannot find Grammar section with ID = " + questionFillBlankDTO.getGrammarSectionId()))
                    : null;

            QuestionFillBlank question = modelMapper.map(questionFillBlankDTO, QuestionFillBlank.class);
            question.setId(UUID.randomUUID().toString());
            question.setLesson(existingLesson);
            question.setGrammarSection(existingGrammarSection);

            if (questionFillBlankDTO.getImage() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionFillBlankDTO.getImage(), "image");
                question.setImageUrl(cloudinaryResponse.getUrl());
                question.setImageName(cloudinaryResponse.getPublicId());
            }

            if (questionFillBlankDTO.getVideo() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionFillBlankDTO.getVideo(), "video");
                question.setVideoUrl(cloudinaryResponse.getUrl());
                question.setVideoName(cloudinaryResponse.getPublicId());
            }

            if (questionFillBlankDTO.getAudio() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionFillBlankDTO.getAudio(), "audio");
                question.setAudioUrl(cloudinaryResponse.getUrl());
                question.setAudioName(cloudinaryResponse.getPublicId());
            }

            question = questionFillBlankRepository.save(question);

            QuestionFillBlankResponse response = modelMapper.map(question, QuestionFillBlankResponse.class);
            response.setLessonId(question.getLesson() != null ? question.getLesson().getId() : null);
            response.setGrammarSectionId(question.getGrammarSection() != null ? question.getGrammarSection().getId() : null);
            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create question fill blank, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_QUESTION, requestId);
        }
    }

    @Override
    public QuestionFillBlankResponse getQuestion(String requestId, String questionId) {
        try {

            QuestionFillBlank existingQuestion = questionFillBlankRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));

            QuestionFillBlankResponse response = modelMapper.map(existingQuestion, QuestionFillBlankResponse.class);
            response.setLessonId(existingQuestion.getLesson() != null ? existingQuestion.getLesson().getId() : null);
            response.setGrammarSectionId(existingQuestion.getGrammarSection() != null ? existingQuestion.getGrammarSection().getId() : null);
            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to get question fill blank, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public QuestionFillBlankResponse updateQuestion(String requestId, String questionId, QuestionFillBlankDTO questionFillBlankDTO) {
        try {

            QuestionFillBlank existingQuestion = questionFillBlankRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));

            if (!existingQuestion.getLesson().getId().equals(questionFillBlankDTO.getLessonId())) {
                Lesson existingLesson = lessonRepository.findById(questionFillBlankDTO.getLessonId())
                        .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + questionFillBlankDTO.getLessonId()));
                existingQuestion.setLesson(existingLesson);
            }

            if (!existingQuestion.getGrammarSection().getId().equals(questionFillBlankDTO.getGrammarSectionId())) {
                GrammarSection existingGrammarSection = grammarSectionRepository.findById(questionFillBlankDTO.getGrammarSectionId())
                                .orElseThrow(() -> new DataNotFoundException("Cannot find Grammar section with ID = " + questionFillBlankDTO.getGrammarSectionId()));
                existingQuestion.setGrammarSection(existingGrammarSection);
            }
            modelMapper.map(questionFillBlankDTO, existingQuestion);

            if (questionFillBlankDTO.getImage() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionFillBlankDTO.getImage(), "image");
                existingQuestion.setImageUrl(cloudinaryResponse.getUrl());
                existingQuestion.setImageName(cloudinaryResponse.getPublicId());
            }

            if (questionFillBlankDTO.getVideo() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionFillBlankDTO.getVideo(), "video");
                existingQuestion.setVideoUrl(cloudinaryResponse.getUrl());
                existingQuestion.setVideoName(cloudinaryResponse.getPublicId());
            }

            if (questionFillBlankDTO.getAudio() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionFillBlankDTO.getAudio(), "audio");
                existingQuestion.setAudioUrl(cloudinaryResponse.getUrl());
                existingQuestion.setAudioName(cloudinaryResponse.getPublicId());
            }

            existingQuestion = questionFillBlankRepository.save(existingQuestion);

            QuestionFillBlankResponse response = modelMapper.map(existingQuestion, QuestionFillBlankResponse.class);
            response.setLessonId(existingQuestion.getLesson() != null ? existingQuestion.getLesson().getId() : null);
            response.setGrammarSectionId(existingQuestion.getGrammarSection() != null ? existingQuestion.getGrammarSection().getId() : null);
            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to update question fill blank, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteQuestion(String requestId, String questionId) {
        try {
            questionFillBlankRepository.deleteById(questionId);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to delete question fill blank, err="+e.getMessage());
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
