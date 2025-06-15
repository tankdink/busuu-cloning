package com.busuu.app.services.question.multiple_choice;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.questions.QuestionDTO;
import com.busuu.app.dtos.requests.questions.multiple_choice.QuestionMultipleChoiceDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.question.multiple_choice.MultipleChoiceOptionResponse;
import com.busuu.app.dtos.responses.question.multiple_choice.QuestionMultipleChoiceResponse;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.questions.matching.MatchingPair;
import com.busuu.app.entities.questions.multiple_choice.MultipleChoiceOption;
import com.busuu.app.entities.questions.multiple_choice.QuestionMultipleChoice;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.grammar.GrammarSectionRepository;
import com.busuu.app.repositories.LessonRepository;
import com.busuu.app.repositories.questions.mutilple_choice.MultipleChoiceOptionRepository;
import com.busuu.app.repositories.questions.mutilple_choice.QuestionMultipleChoiceRepository;
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
public class QuestionMultipleChoiceService implements IQuestionMultipleChoiceService {
    private final QuestionMultipleChoiceRepository questionMultipleChoiceRepository;
    private final MultipleChoiceOptionRepository multipleChoiceOptionRepository;
    private final LessonRepository lessonRepository;
    private final GrammarSectionRepository grammarSectionRepository;
    private final IUploadCloudinaryService uploadCloudinaryService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public QuestionMultipleChoiceResponse insertQuestion(String requestId, QuestionMultipleChoiceDTO questionDTO) {
        try {
            Lesson existingLesson = questionDTO.getLessonId() != null ?
                    lessonRepository.findById(questionDTO.getLessonId()).orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + questionDTO.getLessonId()))
                    : null;
            GrammarSection existingGrammarSection = questionDTO.getGrammarSectionId() != null ?
                    grammarSectionRepository.findById(questionDTO.getGrammarSectionId()).orElseThrow(() -> new DataNotFoundException("Cannot find Grammar section with ID = " + questionDTO.getGrammarSectionId()))
                    : null;

            QuestionMultipleChoice question = modelMapper.map(questionDTO, QuestionMultipleChoice.class);
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
            QuestionMultipleChoice finalQuestion = question;
            question.getOptions().forEach(
                    option -> {
                        option.setId(UUID.randomUUID().toString());
                        option.setQuestionMultipleChoice(finalQuestion);
                    }
            );

            question = questionMultipleChoiceRepository.save(question);

            QuestionMultipleChoiceResponse response = modelMapper.map(question, QuestionMultipleChoiceResponse.class);
            response.setLessonId(question.getLesson() != null ? question.getLesson().getId() : null);
            response.setGrammarSectionId(question.getGrammarSection() != null ? question.getGrammarSection().getId() : null);

            // Handle ans res
            response.setOptions(question.getOptions().stream().map(
                    option -> modelMapper.map(option, MultipleChoiceOptionResponse.class)
            ).toList());
            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create question multiple choice, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_QUESTION, requestId);
        }
    }

    @Override
    public QuestionMultipleChoiceResponse getQuestion(String requestId, String questionId) {
        try {
            QuestionMultipleChoice existingQuestion = questionMultipleChoiceRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));

            QuestionMultipleChoiceResponse response = modelMapper.map(existingQuestion, QuestionMultipleChoiceResponse.class);
            response.setLessonId(existingQuestion.getLesson() != null ? existingQuestion.getLesson().getId() : null);
            response.setGrammarSectionId(existingQuestion.getGrammarSection() != null ? existingQuestion.getGrammarSection().getId() : null);
            response.setOptions(existingQuestion.getOptions().stream().map(
                    multipleChoiceOption ->  modelMapper.map(multipleChoiceOption, MultipleChoiceOptionResponse.class)
                    ).toList()
            );

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to get question multiple choice, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public QuestionMultipleChoiceResponse updateQuestion(String requestId, String questionId, QuestionMultipleChoiceDTO questionDTO) {
        try {

            QuestionMultipleChoice existingQuestion = questionMultipleChoiceRepository.findById(questionId)
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
            existingQuestion.getOptions().clear();

            QuestionMultipleChoice finalExistingQuestion = existingQuestion;
            questionDTO.getOptions().forEach(optionDto -> {
                MultipleChoiceOption option = modelMapper.map(optionDto, MultipleChoiceOption.class);
                option.setId(UUID.randomUUID().toString());
                option.setQuestionMultipleChoice(finalExistingQuestion);
                finalExistingQuestion.getOptions().add(option);
            });

            existingQuestion = questionMultipleChoiceRepository.save(existingQuestion);

            QuestionMultipleChoiceResponse response = modelMapper.map(existingQuestion, QuestionMultipleChoiceResponse.class);
            response.setLessonId(existingQuestion.getLesson() != null ? existingQuestion.getLesson().getId() : null);
            response.setGrammarSectionId(existingQuestion.getGrammarSection() != null ? existingQuestion.getGrammarSection().getId() : null);
            response.setOptions(existingQuestion.getOptions().stream().map(
                    multipleChoiceOption -> modelMapper.map(multipleChoiceOption, MultipleChoiceOptionResponse.class)
                    ).toList()
            );
            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to update question multiple choice, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteQuestion(String requestId, String questionId) {
        try {
            questionMultipleChoiceRepository.deleteById(questionId);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to delete question multiple choice, err="+e.getMessage());
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
