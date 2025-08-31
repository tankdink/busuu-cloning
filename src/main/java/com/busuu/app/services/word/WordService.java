package com.busuu.app.services.word;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.word.WordDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.WordExampleResponse;
import com.busuu.app.dtos.responses.WordResponse;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.StrengthLevel;
import com.busuu.app.entities.User;
import com.busuu.app.entities.UserWord;
import com.busuu.app.entities.Word;
import com.busuu.app.entities.WordExample;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.LessonRepository;
import com.busuu.app.repositories.UserWordRepository;
import com.busuu.app.repositories.WordRepository;
import com.busuu.app.services.client.OpenAiTranslationService;
import com.busuu.app.services.cloudinary.UploadCloudinaryService;
import com.busuu.app.utils.UploadCloudinaryUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordService implements IWordService {

    private final WordRepository wordRepository;
    private final LessonRepository lessonRepository;
    private final UploadCloudinaryService uploadCloudinaryService;
    private final ModelMapper modelMapper;

    private final OpenAiTranslationService openAiTranslationService;

    @Override
    @Transactional
    public WordResponse createWord(String requestId, WordDTO wordDTO) {
        try {
            if (wordDTO.getAudio() == null || wordDTO.getImage() == null || wordDTO.getAudioVocab() == null) {
                throw new Exception("Audio, Video cannot be null.");
            }

            Lesson extLesson = lessonRepository.findById(wordDTO.getLessonId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID " + wordDTO.getLessonId()));

            // Get code from Lesson
            String code = extLesson.getChapter().getCourse().getLanguage().getCode();

            Word word = modelMapper.map(wordDTO, Word.class);
            word.setId(UUID.randomUUID().toString());
            word.setLesson(extLesson);
            word.setLanguageCode(code);

            // Image
            CloudinaryResponse imageRes = uploadMedia(wordDTO.getImage(), "image");
            word.setImageUrl(imageRes.getUrl());
            word.setImageName(imageRes.getPublicId());

            // Audio Vocab
            CloudinaryResponse audioVocabRes = uploadMedia(wordDTO.getAudioVocab(), "audio");
            word.setAudioUrl(audioVocabRes.getUrl());
            word.setAudioName(audioVocabRes.getPublicId());


            // Word Example
            WordExample wordExample = WordExample.builder()
                    .id(UUID.randomUUID().toString())
                    .word(word)
                    .originalText(wordDTO.getOriginalText())
                    .translateText(wordDTO.getTranslateText())
                    .build();

            // Audio
            CloudinaryResponse audioRes = uploadMedia(wordDTO.getAudio(), "audio");
            wordExample.setAudioUrl(audioRes.getUrl());
            wordExample.setAudioName(audioRes.getPublicId());

            // Add word example into word
            word.setExamples(Collections.singletonList(wordExample));

            word = wordRepository.save(word);

            WordResponse res = modelMapper.map(word, WordResponse.class);
            res.setLessonId(word.getLesson().getId());

            Word finalWord = word;
            res.setExamples(word.getExamples().stream().map(
                    example -> {
                        WordExampleResponse wordExampleResponse = modelMapper.map(example, WordExampleResponse.class);
                        wordExampleResponse.setWordId(finalWord.getId());
                        return wordExampleResponse;
                    }
            ).toList());

            return res;
        } catch (Exception e) {
            log.error("requestId={}, failed to create new word, err={}", requestId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_WORD, requestId);
        }
    }

    @Override
    @Cacheable(
            value = "word-cache",
            key = "'word:' + #wordId + ':lang:' + #languageCode"
    )
    public WordResponse getWord(String requestId, String wordId, String languageCodeDes) {
        try {

            Word extWord = wordRepository.findById(wordId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Word with ID " + wordId));

//            String translateText = openAiTranslationService.translateWord(extWord.getText(), extWord.getLanguageCode(), languageCodeDes);

            List<WordExampleResponse> examples = extWord.getExamples().stream()
                    .map(example -> {
//                        String translateExampleText = openAiTranslationService.translateSentence(example.getOriginalText(), extWord.getLanguageCode(), languageCodeDes);
                        WordExampleResponse res = modelMapper.map(example, WordExampleResponse.class);
                        res.setWordId(extWord.getId());
//                        res.setTranslateText(translateExampleText);

                        return res;
                    }).toList();

            WordResponse wordResponse = modelMapper.map(extWord, WordResponse.class);
            wordResponse.setLessonId(extWord.getLesson().getId());
//            wordResponse.setTranslation(translateText);
            wordResponse.setExamples(examples);

            return wordResponse;
        } catch (Exception e) {
            log.error("requestId={},failed to get word with ID {}, err={}", requestId, wordId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_WORD, requestId);
        }
    }

    @Override
    public Page<WordResponse> getWords(String requestId, Pageable pageable, String lessonId, String keyword) {
        try {
            Page<Word> words = wordRepository.findAllWithFilter(lessonId, keyword, pageable);

            return words.map(word -> {
                List<WordExampleResponse> examples = word.getExamples().stream()
                        .map(example -> {
                            WordExampleResponse res = modelMapper.map(example, WordExampleResponse.class);
                            res.setWordId(word.getId());
                            return res;
                        }).toList();
                WordResponse res = modelMapper.map(word, WordResponse.class);
                res.setLessonId(word.getLesson().getId());
                res.setExamples(examples);
                return res;
            });
        } catch (Exception e) {
            log.error("requestId={}, failed to get words, err={}", requestId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_WORD, requestId);
        }
    }

    @Override
    @Transactional
    public WordResponse updateWord(String requestId, String wordId, WordDTO wordDTO) {
        try {
            Word extWord = wordRepository.findById(wordId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Word with ID " + wordId));

            if (!extWord.getLesson().getId().equals(wordDTO.getLessonId())) {
                Lesson extLesson = lessonRepository.findById(wordDTO.getLessonId())
                        .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID " + wordDTO.getLessonId()));
                extWord.setLesson(extLesson);
                extWord.setLanguageCode(extLesson.getChapter().getCourse().getLanguage().getCode());
            }

            if (wordDTO.getImage() != null) {
                boolean isRemove = true;
                if (extWord.getImageName() != null) {
                    isRemove = uploadCloudinaryService.removeFile(extWord.getImageName());
                }
                if (isRemove) {
                    CloudinaryResponse cloudinaryResponse = uploadMedia(wordDTO.getImage(), "image");
                    if (cloudinaryResponse != null) {
                        extWord.setImageUrl(cloudinaryResponse.getUrl());
                        extWord.setImageName(cloudinaryResponse.getPublicId());
                    }
                }
            }

            if (wordDTO.getAudioVocab() != null) {
                boolean isRemove = true;
                if (extWord.getAudioName() != null) {
                    isRemove = uploadCloudinaryService.removeFile(extWord.getAudioName());
                }
                if (isRemove) {
                    CloudinaryResponse cloudinaryResponse = uploadMedia(wordDTO.getAudioVocab(), "audio");
                    if (cloudinaryResponse != null) {
                        extWord.setAudioUrl(cloudinaryResponse.getUrl());
                        extWord.setAudioName(cloudinaryResponse.getPublicId());
                    }
                }
            }

            WordExample wordExample = extWord.getExamples().get(0);

            if (wordDTO.getAudio() != null) {
                boolean isRemove = true;
                if (wordExample.getAudioName() != null) {
                    isRemove = uploadCloudinaryService.removeFile(wordExample.getAudioName());
                }
                if (isRemove) {
                    CloudinaryResponse cloudinaryResponse = uploadMedia(wordDTO.getAudio(), "audio");
                    if (cloudinaryResponse != null) {
                        wordExample.setAudioUrl(cloudinaryResponse.getUrl());
                        wordExample.setAudioName(cloudinaryResponse.getPublicId());
                    }
                }
            }

            wordExample.setOriginalText(wordDTO.getOriginalText());
            wordExample.setTranslateText(wordDTO.getTranslateText());

            extWord = wordRepository.save(extWord);

            WordExampleResponse exampleRes = modelMapper.map(wordExample, WordExampleResponse.class);
            exampleRes.setWordId(extWord.getId());


            WordResponse wordResponse = modelMapper.map(extWord, WordResponse.class);
            wordResponse.setLessonId(extWord.getLesson().getId());
            wordResponse.setExamples(Collections.singletonList(exampleRes));

            return wordResponse;
        } catch (Exception e) {
            log.error("requestId={},failed to update word with ID = {}, err={}", requestId, wordId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_WORD, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteWord(String requestId, String wordId) {
        try {
            wordRepository.deleteById(wordId);
        } catch (Exception e) {
            log.error("requestId={},failed to delete word with ID = {}, err={}", requestId, wordId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_WORD, requestId);
        }
    }

    @Override
    public Page<WordResponse> getWordsByLesson(String requestId, String lessonId, Pageable pageable) {
        Page<Word> words = wordRepository.findByLessonId(lessonId, pageable);

        return words.map(word -> {
            List<WordExampleResponse> examples = word.getExamples().stream()
                    .map(example -> {
                        WordExampleResponse res = modelMapper.map(example, WordExampleResponse.class);
                        res.setWordId(word.getId());
                        return res;
                    }).toList();
            WordResponse res = modelMapper.map(word, WordResponse.class);
            res.setLessonId(word.getLesson().getId());
            res.setExamples(examples);
            return res;
        });
    }

    private CloudinaryResponse uploadMedia(MultipartFile file, String type) throws Exception {
        UploadCloudinaryUtil.assertAllowed(file, type);
        String fileName = UploadCloudinaryUtil.getFileName(file.getOriginalFilename());
        CloudinaryResponse response = uploadCloudinaryService.uploadFile(file, fileName, type);
        return response;
    }
}
