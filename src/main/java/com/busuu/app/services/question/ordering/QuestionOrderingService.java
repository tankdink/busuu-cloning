package com.busuu.app.services.question.ordering;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.questions.QuestionDTO;
import com.busuu.app.dtos.requests.questions.QuestionFillBlankDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.question.ordering.OrderingPartResponse;
import com.busuu.app.dtos.responses.question.ordering.QuestionOrderingResponse;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.questions.ordering.OrderingPart;
import com.busuu.app.entities.questions.ordering.QuestionOrdering;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.GrammarSectionRepository;
import com.busuu.app.repositories.LessonRepository;
import com.busuu.app.repositories.OrderingPartRepository;
import com.busuu.app.repositories.QuestionOrderingRepository;
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
public class QuestionOrderingService implements IQuestionOrderingService {
    private final QuestionOrderingRepository questionOrderingRepository;
    private final OrderingPartRepository orderingPartRepository;
    private final LessonRepository lessonRepository;
    private final GrammarSectionRepository grammarSectionRepository;
    private final IUploadCloudinaryService uploadCloudinaryService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public QuestionOrderingResponse insertQuestion(String requestId, QuestionFillBlankDTO questionDTO) {
        try {
            Lesson existingLesson = questionDTO.getLessonId() != null ?
                    lessonRepository.findById(questionDTO.getLessonId()).orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + questionDTO.getLessonId()))
                    : null;
            GrammarSection existingGrammarSection = questionDTO.getGrammarSectionId() != null ?
                    grammarSectionRepository.findById(questionDTO.getGrammarSectionId()).orElseThrow(() -> new DataNotFoundException("Cannot find Grammar section with ID = " + questionDTO.getGrammarSectionId()))
                    : null;

            QuestionOrdering question = modelMapper.map(questionDTO, QuestionOrdering.class);
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

            question = questionOrderingRepository.save(question);

            QuestionOrderingResponse response = modelMapper.map(question, QuestionOrderingResponse.class);
            response.setLessonId(question.getLesson() != null ? question.getLesson().getId() : null);
            response.setGrammarSectionId(question.getGrammarSection() != null ? question.getGrammarSection().getId() : null);

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create question ordering, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_QUESTION, requestId);
        }
    }

    @Override
    public QuestionOrderingResponse getQuestion(String requestId, String questionId) {
        try {
            QuestionOrdering existingQuestion = questionOrderingRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));

            List<OrderingPart> existingOrderingParts = orderingPartRepository.findByQuestionOrdering(existingQuestion);

            QuestionOrderingResponse response = modelMapper.map(existingQuestion, QuestionOrderingResponse.class);
            response.setLessonId(existingQuestion.getLesson() != null ? existingQuestion.getLesson().getId() : null);
            response.setGrammarSectionId(existingQuestion.getGrammarSection() != null ? existingQuestion.getGrammarSection().getId() : null);
            response.setParts(existingOrderingParts.stream().map(
                    orderingPart -> {
                        OrderingPartResponse orderingPartResponse = modelMapper.map(orderingPart, OrderingPartResponse.class);
                        orderingPartResponse.setQuestionId(response.getId());
                        return orderingPartResponse;
                    }).toList()
            );

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to get question ordering, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public QuestionOrderingResponse updateQuestion(String requestId, String questionId, QuestionFillBlankDTO questionDTO) {
        try {

            QuestionOrdering existingQuestion = questionOrderingRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));

            if (!existingQuestion.getLesson().getId().equals(questionDTO.getLessonId())) {
                Lesson existingLesson = lessonRepository.findById(questionDTO.getLessonId())
                        .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + questionDTO.getLessonId()));
                existingQuestion.setLesson(existingLesson);
            }

            if (!existingQuestion.getGrammarSection().getId().equals(questionDTO.getGrammarSectionId())) {
                GrammarSection existingGrammarSection = grammarSectionRepository.findById(questionDTO.getGrammarSectionId())
                        .orElseThrow(() -> new DataNotFoundException("Cannot find Grammar section with ID = " + questionDTO.getGrammarSectionId()));
                existingQuestion.setGrammarSection(existingGrammarSection);
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

            existingQuestion = questionOrderingRepository.save(existingQuestion);

            List<OrderingPart> existingOrderingParts = orderingPartRepository.findByQuestionOrdering(existingQuestion);

            QuestionOrderingResponse response = modelMapper.map(existingQuestion, QuestionOrderingResponse.class);
            response.setLessonId(existingQuestion.getLesson() != null ? existingQuestion.getLesson().getId() : null);
            response.setGrammarSectionId(existingQuestion.getGrammarSection() != null ? existingQuestion.getGrammarSection().getId() : null);
            response.setParts(existingOrderingParts.stream().map(
                    orderingPart -> {
                        OrderingPartResponse orderingPartResponse = modelMapper.map(orderingPart, OrderingPartResponse.class);
                        orderingPartResponse.setQuestionId(response.getId());
                        return orderingPartResponse;
                    }).toList()
            );
            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to update question ordering, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteQuestion(String requestId, String questionId) {
        try {
            questionOrderingRepository.deleteById(questionId);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to delete question ordering, err="+e.getMessage());
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
