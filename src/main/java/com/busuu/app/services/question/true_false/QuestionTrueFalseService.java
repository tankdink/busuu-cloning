package com.busuu.app.services.question.true_false;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.questions.QuestionTrueFalseDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.question.QuestionTrueFalseResponse;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.questions.QuestionTrueFalse;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.GrammarSectionRepository;
import com.busuu.app.repositories.LessonRepository;
import com.busuu.app.repositories.QuestionTrueFalseRepository;
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
public class QuestionTrueFalseService implements IQuestionTrueFalseService {
    private final QuestionTrueFalseRepository questionTrueFalseRepository;
    private final LessonRepository lessonRepository;
    private final GrammarSectionRepository grammarSectionRepository;
    private final IUploadCloudinaryService uploadCloudinaryService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public QuestionTrueFalseResponse insertQuestion(String requestId, QuestionTrueFalseDTO questionTrueFalseDTO) {
        try {
            Lesson existingLesson = questionTrueFalseDTO.getLessonId() != null ?
                    lessonRepository.findById(questionTrueFalseDTO.getLessonId()).orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + questionTrueFalseDTO.getLessonId()))
                    : null;
            GrammarSection existingGrammarSection = questionTrueFalseDTO.getGrammarSectionId() != null ?
                    grammarSectionRepository.findById(questionTrueFalseDTO.getGrammarSectionId()).orElseThrow(() -> new DataNotFoundException("Cannot find Grammar section with ID = " + questionTrueFalseDTO.getGrammarSectionId()))
                    : null;

            QuestionTrueFalse question = modelMapper.map(questionTrueFalseDTO, QuestionTrueFalse.class);
            question.setId(UUID.randomUUID().toString());
            question.setLesson(existingLesson);
            question.setGrammarSection(existingGrammarSection);

            if (questionTrueFalseDTO.getImage() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionTrueFalseDTO.getImage(), "image");
                question.setImageUrl(cloudinaryResponse.getUrl());
                question.setImageName(cloudinaryResponse.getPublicId());
            }

            if (questionTrueFalseDTO.getVideo() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionTrueFalseDTO.getVideo(), "video");
                question.setVideoUrl(cloudinaryResponse.getUrl());
                question.setVideoName(cloudinaryResponse.getPublicId());
            }

            if (questionTrueFalseDTO.getAudio() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionTrueFalseDTO.getAudio(), "audio");
                question.setAudioUrl(cloudinaryResponse.getUrl());
                question.setAudioName(cloudinaryResponse.getPublicId());
            }

            question = questionTrueFalseRepository.save(question);

            QuestionTrueFalseResponse response =  modelMapper.map(question, QuestionTrueFalseResponse.class);
            response.setLessonId(question.getLesson() != null ? question.getLesson().getId() : null);
            response.setGrammarSectionId(question.getGrammarSection() != null ? question.getGrammarSection().getId() : null);
            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create question true false, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_QUESTION, requestId);
        }
    }

    @Override
    public QuestionTrueFalseResponse getQuestion(String requestId, String questionId) {
        try {
            QuestionTrueFalse existingQuestion = questionTrueFalseRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));

            QuestionTrueFalseResponse response = modelMapper.map(existingQuestion, QuestionTrueFalseResponse.class);
            response.setLessonId(existingQuestion.getLesson() != null ? existingQuestion.getLesson().getId() : null);
            response.setGrammarSectionId(existingQuestion.getGrammarSection() != null ? existingQuestion.getGrammarSection().getId() : null);

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to get question true false, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public QuestionTrueFalseResponse updateQuestion(String requestId, String questionId, QuestionTrueFalseDTO questionTrueFalseDTO) {
        try {
            QuestionTrueFalse existingQuestion = questionTrueFalseRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));

            if (!existingQuestion.getLesson().getId().equals(questionTrueFalseDTO.getLessonId())) {
                Lesson existingLesson = lessonRepository.findById(questionTrueFalseDTO.getLessonId())
                        .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + questionTrueFalseDTO.getLessonId()));
                existingQuestion.setLesson(existingLesson);
            }

            if (!existingQuestion.getGrammarSection().getId().equals(questionTrueFalseDTO.getGrammarSectionId())) {
                GrammarSection existingGrammarSection = grammarSectionRepository.findById(questionTrueFalseDTO.getGrammarSectionId())
                        .orElseThrow(() -> new DataNotFoundException("Cannot find Grammar section with ID = " + questionTrueFalseDTO.getGrammarSectionId()));
                existingQuestion.setGrammarSection(existingGrammarSection);
            }

            modelMapper.map(questionTrueFalseDTO, existingQuestion);

            if (questionTrueFalseDTO.getImage() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionTrueFalseDTO.getImage(), "image");
                existingQuestion.setImageUrl(cloudinaryResponse.getUrl());
                existingQuestion.setImageName(cloudinaryResponse.getPublicId());
            }

            if (questionTrueFalseDTO.getVideo() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionTrueFalseDTO.getVideo(), "video");
                existingQuestion.setVideoUrl(cloudinaryResponse.getUrl());
                existingQuestion.setVideoName(cloudinaryResponse.getPublicId());
            }

            if (questionTrueFalseDTO.getAudio() != null) {
                CloudinaryResponse cloudinaryResponse = uploadMedia(questionTrueFalseDTO.getAudio(), "audio");
                existingQuestion.setAudioUrl(cloudinaryResponse.getUrl());
                existingQuestion.setAudioName(cloudinaryResponse.getPublicId());
            }

            existingQuestion = questionTrueFalseRepository.save(existingQuestion);

            QuestionTrueFalseResponse response =  modelMapper.map(existingQuestion, QuestionTrueFalseResponse.class);
            response.setLessonId(existingQuestion.getLesson() != null ? existingQuestion.getLesson().getId() : null);
            response.setGrammarSectionId(existingQuestion.getGrammarSection() != null ? existingQuestion.getGrammarSection().getId() : null);
            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to update question true false, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_QUESTION, requestId);
        }
    }

    @Override
    public void deleteQuestion(String requestId, String questionId) {
        try {
            questionTrueFalseRepository.deleteById(questionId);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to delete question true false, err="+e.getMessage());
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
