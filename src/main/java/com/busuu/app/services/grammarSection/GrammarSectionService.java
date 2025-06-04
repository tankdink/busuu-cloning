package com.busuu.app.services.grammarSection;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.section.GrammarSectionDTO;
import com.busuu.app.dtos.responses.GrammarSectionResponse;
import com.busuu.app.entities.Grammar;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.Level;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.grammar.GrammarRepository;
import com.busuu.app.repositories.grammar.GrammarSectionRepository;
import com.busuu.app.repositories.LessonRepository;
import com.busuu.app.repositories.LevelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrammarSectionService implements IGrammarSectionService
{
    private final ModelMapper modelMapper;

    private final GrammarSectionRepository grammarSectionRepository;

    private final GrammarRepository grammarRepository;
    
    private final LessonRepository lessonRepository;

    private final LevelRepository levelRepository;
    
    
    @Override
    @Transactional
    public GrammarSectionResponse insertGrammarSection(String requestId, GrammarSectionDTO grammarSectionDTO) 
    {
        try {

            //Check and get exists lesson, grammar, level
            Lesson lesson = null;
            if (grammarSectionDTO.getLessonId() != null && !grammarSectionDTO.getLevelId().isEmpty()) {
                 lesson = lessonRepository.findById(grammarSectionDTO.getLessonId())
                        .orElseThrow( ()-> new DataNotFoundException("Cannot find lesson with ID " + grammarSectionDTO.getLessonId()) );
            }

            Grammar grammar = grammarRepository.findById(grammarSectionDTO.getGrammarId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find grammar with ID " + grammarSectionDTO.getGrammarId()) );

            Level level = levelRepository.findById(grammarSectionDTO.getLevelId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find level with ID " + grammarSectionDTO.getLevelId()) );


            //Check duplicate order, name
//            if (grammarSectionRepository.existsByGrammarSectionOrderAndGrammarId(grammarSectionDTO.getGrammarSectionOrder(), grammar.getId())) {
//                throw new ExistDataException("Grammar Section's order is duplicated");
//            }

            if (grammarSectionRepository.existsByTitle(grammarSectionDTO.getTitle())) throw new ExistDataException("Grammar Section Title is duplicated");


            //Convert DTO to entity
            GrammarSection newGrammarSection = modelMapper.map(grammarSectionDTO, GrammarSection.class);


            //Generate ID for new grammar section
            newGrammarSection.setId(UUID.randomUUID().toString());

            //Set lesson for new grammar section
            newGrammarSection.setLesson(lesson);

            //Set course for new grammar section
            newGrammarSection.setGrammar(grammar);

            //Set level for new grammar section
            newGrammarSection.setLevel(level);

            // Set grammar section order max + 1
            Integer grammarSectionOrder = grammarSectionRepository.findMaxGrammarSectionOrderByGrammarId(grammar.getId());
            if (grammarSectionOrder == null) {
                newGrammarSection.setGrammarSectionOrder(1);
            } else {
                newGrammarSection.setGrammarSectionOrder(grammarSectionOrder + 1);
            }

            //Save, map + add additional properties and return new GrammarSection
            GrammarSectionResponse savedGrammarSection = modelMapper.map(grammarSectionRepository.save(newGrammarSection), GrammarSectionResponse.class);
            savedGrammarSection.setGrammarId(grammar.getId());
            savedGrammarSection.setLessonId(lesson != null ? lesson.getId() : null);
            savedGrammarSection.setLevelId(level.getId());

            return savedGrammarSection;


        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create new grammar section, err= "+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_NEW_GRAMMAR_SECTION, requestId);
        }
    }

    @Override
    public Page<GrammarSectionResponse>getGrammarSections(String requestId, int page, int size, String sortBy, String sortDirection) {
        try {

            //Pageable
            Sort sort = Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection)));
            Pageable pageable = PageRequest.of(page, size, sort);

            //Get all, mapping and return
            return (grammarSectionRepository.findAll(pageable))
                    .map(grammarSection ->
                    {
                        GrammarSectionResponse response = modelMapper.map(grammarSection, GrammarSectionResponse.class);

                        response.setGrammarId(grammarSection.getGrammar().getId());
                        if (grammarSection.getLesson() != null ) response.setLessonId(grammarSection.getLesson().getId());
                        response.setLevelId(grammarSection.getLevel().getId());

                        return response;

                    });


        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get grammar sections list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_ALL_GRAMMAR_SECTION, requestId);
        }
    }

    @Override
    public GrammarSectionResponse getGrammarSection(String requestId, String grammarSectionId)
    {
        try {

            GrammarSection gettedGrammarSection = grammarSectionRepository.findById(grammarSectionId)
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find grammar section with ID " + grammarSectionId) );


            //Return
            GrammarSectionResponse response = modelMapper.map(gettedGrammarSection, GrammarSectionResponse.class);
            response.setGrammarId(gettedGrammarSection.getGrammar().getId());
            response.setLessonId(gettedGrammarSection.getLesson() != null ? gettedGrammarSection.getLesson().getId() : null);
            response.setLevelId(gettedGrammarSection.getLevel().getId());
            return response;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get grammar section with ID " +  grammarSectionId + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_GRAMMAR_SECTION_BY_ID, requestId);
        }
    }

    @Override
    public List<GrammarSectionResponse> getByGrammarId(String requestId, String grammarId)
    {
        try {

            List<GrammarSection> gettedGrammarSectionList = grammarSectionRepository.findByGrammarId(grammarId);
            if (gettedGrammarSectionList.isEmpty()) throw new DataNotFoundException("No grammar section found with grammar ID " + grammarId);


            //Return
            return (gettedGrammarSectionList.stream()
                    .map(grammarSection ->
                    {
                        GrammarSectionResponse response = modelMapper.map(grammarSection, GrammarSectionResponse.class);

                        response.setGrammarId(grammarSection.getGrammar().getId());
                        response.setLessonId(grammarSection.getLesson() != null ? grammarSection.getLesson().getId() : null);
                        response.setLevelId(grammarSection.getLevel().getId());

                        return response;
                    })
                    .collect(Collectors.toList()));


        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get grammar section with grammar ID " +  grammarId + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_GRAMMAR_SECTION_BY_GRAMMAR_ID, requestId);
        }
    }

    @Override
    @Transactional
    public GrammarSectionResponse updateGrammarSection(String requestId, String grammarSectionId, GrammarSectionDTO infoUpdateGrammarSection)
    {
        try {

            //Check exists grammar section/Get update grammar section
            GrammarSection existingGrammarSection = grammarSectionRepository.findById(grammarSectionId)
                    .orElseThrow( ()-> new DataNotFoundException("No grammar section found with ID " + grammarSectionId) );

            //Check exists lesson, grammar, level and duplicate order, title
            Lesson lesson = null;
            if (infoUpdateGrammarSection.getLessonId() != null) {
                lesson = lessonRepository.findById(infoUpdateGrammarSection.getLessonId())
                        .orElseThrow( ()-> new DataNotFoundException("Cannot find lesson with ID " + infoUpdateGrammarSection.getLessonId()) );
            }

            Grammar grammar = grammarRepository.findById(infoUpdateGrammarSection.getGrammarId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find grammar with ID " + infoUpdateGrammarSection.getGrammarId()) );

            Level level = levelRepository.findById(infoUpdateGrammarSection.getLevelId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find level with ID " + infoUpdateGrammarSection.getLevelId()) );

            if (!Objects.equals(existingGrammarSection.getGrammarSectionOrder(), infoUpdateGrammarSection.getGrammarSectionOrder())) {
                if (grammarSectionRepository.existsByGrammarSectionOrderAndGrammarId(infoUpdateGrammarSection.getGrammarSectionOrder(), grammar.getId())) {
                    throw new ExistDataException("Grammar Section's order is duplicated");
                }
            }

            if (!Objects.equals(existingGrammarSection.getTitle(), infoUpdateGrammarSection.getTitle())) {
                if (grammarSectionRepository.existsByTitle(infoUpdateGrammarSection.getTitle()))
                    throw new ExistDataException("Grammar Section Title is duplicated");
            }

            //Update
            modelMapper.map(infoUpdateGrammarSection, existingGrammarSection);
            existingGrammarSection.setLesson(lesson);
            existingGrammarSection.setGrammar(grammar);
            existingGrammarSection.setLevel(level);

            //Save and return
            GrammarSectionResponse response = modelMapper.map(grammarSectionRepository.save(existingGrammarSection), GrammarSectionResponse.class);
            response.setGrammarId(existingGrammarSection.getGrammar().getId());
            response.setLessonId(existingGrammarSection.getLesson() != null ? existingGrammarSection.getLesson().getId() : null);
            response.setLevelId(existingGrammarSection.getLevel().getId());

            return response;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to update grammar section with ID " +  grammarSectionId + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_GRAMMAR_SECTION_BY_ID, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteGrammarSection(String requestId, String grammarSectionId)
    {
        try {

            //Check exist grammar section
            GrammarSection existingGrammarSection = grammarSectionRepository.findById(grammarSectionId)
                    .orElseThrow( ()-> new DataNotFoundException("No grammar section found with ID " + grammarSectionId) );

            grammarSectionRepository.deleteById(grammarSectionId);

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete grammar section with ID " +  grammarSectionId + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_GRAMMAR_SECTION_BY_ID, requestId);
        }
    }
}
