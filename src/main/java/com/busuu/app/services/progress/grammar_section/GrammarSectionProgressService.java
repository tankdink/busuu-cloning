package com.busuu.app.services.progress.grammar_section;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.GrammarSectionResponse;
import com.busuu.app.dtos.responses.question.SectionLevelStatsResponse;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.SectionLevel;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.GrammarSectionProgress;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.grammar.GrammarSectionRepository;
import com.busuu.app.repositories.progress.GrammarSectionProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrammarSectionProgressService implements IGrammarSectionProgressService {

    private final GrammarSectionProgressRepository grammarSectionProgressRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public GrammarSectionProgress upsertGrammarSectionProgress(GrammarSection section, User user, int numCorrectQuestions) {
        int total = section.getQuestions().size();
        double progress = total == 0 ? 0 : (double) numCorrectQuestions / total * 100;

        GrammarSectionProgress sectionProgress = grammarSectionProgressRepository
                .findByGrammarSectionIdAndUserId(section.getId(), user.getId());

        if (sectionProgress == null) {
            sectionProgress = GrammarSectionProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .grammarSection(section)
                    .user(user)
                    .progress(progress)
                    .isCompleted(progress >= Constants.PASSING_PROGRESS)
                    .level(SectionLevel.fromProgress(progress))
                    .build();
        } else {
            double maxProgress = Math.max(progress, sectionProgress.getProgress());
            sectionProgress.setProgress(maxProgress);
            sectionProgress.setIsCompleted(maxProgress >= Constants.PASSING_PROGRESS);
            sectionProgress.setLevel(SectionLevel.fromProgress(maxProgress));
        }

        return grammarSectionProgressRepository.save(sectionProgress);
    }

    @Override
    public List<SectionLevelStatsResponse> sectionStats(String requestId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            // Get list section level
            SectionLevel[] levels = SectionLevel.values();

            return Arrays.stream(levels).map(level -> {
                long total = grammarSectionProgressRepository.countByLevelAndUserId(level, userId);
                List<GrammarSectionProgress> sectionProgresses = grammarSectionProgressRepository.findByLevelAndUserId(level, userId);
                return SectionLevelStatsResponse.builder()
                        .level(level)
                        .total(total)
                        .grammarSection(sectionProgresses.stream().map(
                                sectionProgress -> {
                                    GrammarSection grammarSection = sectionProgress.getGrammarSection();

                                    GrammarSectionResponse response = modelMapper.map(grammarSection, GrammarSectionResponse.class);

                                    response.setGrammarId(grammarSection.getGrammar().getId());
                                    response.setLessonId(grammarSection.getLesson() != null ? grammarSection.getLesson().getId() : null);
                                    response.setLevelId(grammarSection.getLevel().getId());

                                    response.setIsCompleted(sectionProgress.getIsCompleted());
                                    response.setProgress(sectionProgress.getProgress());

                                    return response;
                                }
                        ).toList())
                        .build();
            }).toList();
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get stats of grammar section , err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_STATS_GRAMMAR_SECTION, requestId);
        }
    }

    @Override
    public long countSectionProgress (String requestId) {
       try {
           Authentication auth = SecurityContextHolder.getContext().getAuthentication();
           User user = (User) auth.getPrincipal();
           String userId = user.getId();

           return grammarSectionProgressRepository.countByUserId(userId);
       } catch (Exception e) {
           log.error("requestId="+requestId+",failed to get count grammar section progress, err="+e.getMessage());
           throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                   Constants.ERROR_CODE.ERR_GET_COUNT_GRAMMAR_SECTION_PROGRESS, requestId);
       }
    }
}
