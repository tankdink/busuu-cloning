package com.busuu.app.services.userWord;

import com.busuu.app.dtos.requests.word.ReviewResultRequest;
import com.busuu.app.dtos.responses.UserWordResponse;
import com.busuu.app.dtos.responses.WordExampleResponse;
import com.busuu.app.dtos.responses.WordFilterResponse;
import com.busuu.app.dtos.responses.WordResponse;
import com.busuu.app.entities.enums.StrengthLevel;
import com.busuu.app.entities.User;
import com.busuu.app.entities.UserWord;
import com.busuu.app.entities.UserWordHistory;
import com.busuu.app.entities.Word;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.repositories.UserWordHistoryRepository;
import com.busuu.app.repositories.UserWordRepository;
import com.busuu.app.services.word.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserWordService implements IUserWordService{

    private final UserWordRepository userWordRepository;
    private final WordService wordService;
    private final UserWordHistoryRepository userWordHistoryRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<WordFilterResponse> listFilter (String requestId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        List<Object[]> counts = userWordRepository.countGroupedByStrengthAndFavorite(user.getId());

        long totalAll = 0;
        long countWeak = 0;
        long countMedium = 0;
        long countStrong = 0;
        long countFavorite = 0;

        for (Object[] row : counts) {
            StrengthLevel level = (StrengthLevel) row[0];
            Boolean fav = (Boolean) row[1];
            Long cnt = (Long) row[2];

            totalAll += cnt;

            if (level == StrengthLevel.WEAK) countWeak += cnt;
            if (level == StrengthLevel.MEDIUM) countMedium += cnt;
            if (level == StrengthLevel.STRONG) countStrong += cnt;
            if (Boolean.TRUE.equals(fav)) countFavorite += cnt;
        }

        List<WordFilterResponse> res = List.of(
                WordFilterResponse.builder().type("All").total(totalAll).build(),
                WordFilterResponse.builder().type("Weak").total(countWeak).build(),
                WordFilterResponse.builder().type("Medium").total(countMedium).build(),
                WordFilterResponse.builder().type("Strong").total(countStrong).build(),
                WordFilterResponse.builder().type("Favorite").total(countFavorite).build()
        );
        return res;
    }

    @Override
    public Page<UserWordResponse> getWordsByUser(String requestId, Pageable pageable, StrengthLevel strengthLevel, Boolean isFavorite) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        List<UserWord> userWords = userWordRepository.findByUserIdWithFilter(user.getId(), strengthLevel, isFavorite);

        List<UserWordResponse> res = new ArrayList<>();
        userWords.forEach(userWord -> {
            WordResponse wordResponse = wordService.getWord(requestId, userWord.getWord().getId(), user.getLanguageCode());

            UserWordResponse userWordResponse = modelMapper.map(userWord, UserWordResponse.class);
            userWordResponse.setWord(wordResponse);
            res.add(userWordResponse);
        });

        // Handle res page
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), res.size());
        List<UserWordResponse> pageContent = start > end ? new ArrayList<>() : res.subList(start, end);

        return new PageImpl<>(pageContent, pageable, res.size());
    }

    @Override
    @Transactional
    public void favoriteWord(String requestId, String userWordId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Optional<UserWord> existingUserWord = userWordRepository.findById(userWordId);

        if (existingUserWord.isPresent()) {
            existingUserWord.get().setIsFavorite(!existingUserWord.get().getIsFavorite());
            existingUserWord = Optional.of(userWordRepository.save(existingUserWord.get()));
        } else {
            throw new DataNotFoundException("Cannot find Word of User");
        }
    }

    @Override
    @Transactional
    public void deleteWord(String requestId, String wordId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Optional<UserWord> existingUserWord = userWordRepository.findByUserIdAndWordId(user.getId(), wordId);

        if (existingUserWord.isPresent()) {
            existingUserWord.get().setIsActive(false);
            existingUserWord = Optional.of(userWordRepository.save(existingUserWord.get()));
        } else {
            throw new DataNotFoundException("Cannot find Word of User");
        }
    }


    @Override
    public List<WordResponse> getReviewWords(String requestId, String type, StrengthLevel strengthLevel) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Pageable pageable = PageRequest.of(0, 10);

        List<Word> words;

        switch (type.toUpperCase()) {
            case "ALL" -> {
                words = userWordRepository.findReviewAll(user.getId(), pageable);
            }
            case "STRENGTH" -> {
                if (strengthLevel == null) {
                    throw new IllegalArgumentException("StrengthLevel is required when type = STRENGTH");
                }
                words = userWordRepository.findReviewByStrength(user.getId(), strengthLevel, pageable);
            }
            case "FAVORITE" -> {
                words = userWordRepository.findReviewFavorite(user.getId(), pageable);
            }
            default -> throw new IllegalArgumentException("Invalid review type: " + type);
        }

        return words.stream().map(word -> {
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
        }).toList();
    }

    @Transactional
    @Override
    public void processReviewResults(List<ReviewResultRequest> results) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        LocalDateTime now = LocalDateTime.now();
        List<UserWord> updatedUserWords = new ArrayList<>();
        List<UserWordHistory> histories = new ArrayList<>();

        for (ReviewResultRequest result : results) {
            UserWord userWord = userWordRepository.findByUserIdAndWordId(user.getId(), result.getWordId())
                    .orElseThrow(() -> new RuntimeException("UserWord not found"));

            StrengthLevel previousLevel = userWord.getStrengthLevel();

            // Update stats
            userWord.setReviewCount(userWord.getReviewCount() + 1);
            userWord.setLastReviewedDate(now);

            if (Boolean.TRUE.equals(result.getIsCorrect())) {
                userWord.setCorrectCount(userWord.getCorrectCount() + 1);
                userWord.setReviewInterval(Math.min(userWord.getReviewInterval() * 2, 30));
            } else {
                userWord.setIncorrectCount(userWord.getIncorrectCount() + 1);
                userWord.setReviewInterval(1);
            }

            userWord.setNextReviewDate(now.plusDays(userWord.getReviewInterval()));

            StrengthLevel newStrength = evaluateStrength(userWord);
            userWord.setStrengthLevel(newStrength);

            updatedUserWords.add(userWord);

            // History
            UserWordHistory history = new UserWordHistory();
            history.setId(UUID.randomUUID().toString());
            history.setUserWord(userWord);
            history.setPreviousStrengthLevel(previousLevel);
            history.setNewStrengthLevel(newStrength);
            history.setResult(Boolean.TRUE.equals(result.getIsCorrect()) ? "CORRECT" : "INCORRECT");
            history.setReviewedAt(Timestamp.valueOf(now));

            histories.add(history);
        }

        userWordRepository.saveAll(updatedUserWords);
        userWordHistoryRepository.saveAll(histories);
    }

    private StrengthLevel evaluateStrength(UserWord userWord) {
        LocalDateTime lastReviewed = userWord.getLastReviewedDate();
        if (lastReviewed == null) {
            return StrengthLevel.WEAK;
        }

        long daysSinceLastReview = Duration.between(lastReviewed, LocalDateTime.now()).toDays();
        int correctCount = userWord.getCorrectCount();
        int incorrectCount = userWord.getIncorrectCount();

        // Strength factor: correct +1, incorrect -1 (min = 1)
        double s = Math.max(1, correctCount * 2 - incorrectCount);

        // Retention theo Ebbinghaus: e^(-t/s)
        double retention = Math.exp(-1.0 * daysSinceLastReview / s);

        // Evaluate StrengthLevel
        if (retention < 0.4 || correctCount <= incorrectCount) {
            return StrengthLevel.WEAK;
        } else if (retention < 0.7) {
            return StrengthLevel.MEDIUM;
        } else {
            return StrengthLevel.STRONG;
        }
    }
}
