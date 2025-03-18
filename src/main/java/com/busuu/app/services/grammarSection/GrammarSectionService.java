package com.busuu.app.services.grammarSection;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.section.GrammarSectionDTO;
import com.busuu.app.dtos.responses.GrammarSectionResponse;
import com.busuu.app.entities.Grammar;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.Lesson;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.GrammarRepository;
import com.busuu.app.repositories.GrammarSectionRepository;
import com.busuu.app.repositories.LessonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    
    
    @Override
    @Transactional
    public GrammarSectionResponse insertGrammarSection(String requestId, GrammarSectionDTO grammarSectionDTO) 
    {
        try {

            //Check and get exists lesson, grammar
            Lesson lesson = lessonRepository.findById(grammarSectionDTO.getLessonId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find lesson with ID " + grammarSectionDTO.getLessonId()) );

            Grammar grammar = grammarRepository.findById(grammarSectionDTO.getGrammarId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find grammar with ID " + grammarSectionDTO.getGrammarId()) );

            //Check duplicate order, name
            if (grammarSectionRepository.existsByGrammarSectionOrderAndGrammarId(grammarSectionDTO.getGrammarSectionOrder(), grammar.getId())) {
                throw new ExistDataException("Grammar Section's order is duplicated");
            }
            if (grammarSectionRepository.existsByTitle(grammarSectionDTO.getTitle())) throw new ExistDataException("Grammar Section Title order is duplicated");




            //Convert DTO to entity
            GrammarSection newGrammarSection = modelMapper.map(grammarSectionDTO, GrammarSection.class);


            //Generate ID for new grammar section
            newGrammarSection.setId(UUID.randomUUID().toString());

            //Set lesson for new grammar section
            newGrammarSection.setLesson(lesson);

            //Set course for new grammar section
            newGrammarSection.setGrammar(grammar);


            //Save, map + add additional properties and return new GrammarSection
            GrammarSectionResponse savedGrammarSection = modelMapper.map(grammarSectionRepository.save(newGrammarSection), GrammarSectionResponse.class);
            savedGrammarSection.setGrammarId(grammar.getId());
            savedGrammarSection.setLessonId(lesson.getId());

            return savedGrammarSection;


        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create new grammar section, err= "+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_NEW_GRAMMAR_SECTION, requestId);
        }
    }

    @Override
    public List<GrammarSectionResponse> getGrammarSections(String requestId) {
        try {

            //Get all, mapping and return
            return (grammarSectionRepository.findAll()).stream()
                    .map(grammarSection ->
                    {
                        GrammarSectionResponse response = modelMapper.map(grammarSection, GrammarSectionResponse.class);

                        response.setGrammarId(grammarSection.getGrammar().getId());
                        response.setLessonId(grammarSection.getLesson().getId());

                        return response;
                    })
                    .collect(Collectors.toList());


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
            response.setLessonId(gettedGrammarSection.getLesson().getId());
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
            if (gettedGrammarSectionList.isEmpty()) throw new DataNotFoundException("Cannot find grammar section with grammar ID " + grammarId);


            //Return
            return (gettedGrammarSectionList.stream()
                    .map(grammarSection ->
                    {
                        GrammarSectionResponse response = modelMapper.map(grammarSection, GrammarSectionResponse.class);

                        response.setGrammarId(grammarSection.getGrammar().getId());
                        response.setLessonId(grammarSection.getLesson().getId());

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

            //Check exists lesson, grammar and duplicate order, title
            Lesson lesson = lessonRepository.findById(infoUpdateGrammarSection.getLessonId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find lesson with ID " + infoUpdateGrammarSection.getLessonId()) );

            Grammar grammar = grammarRepository.findById(infoUpdateGrammarSection.getGrammarId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find grammar with ID " + infoUpdateGrammarSection.getGrammarId()) );

            if (!Objects.equals(existingGrammarSection.getGrammarSectionOrder(), infoUpdateGrammarSection.getGrammarSectionOrder())) {
                if (grammarSectionRepository.existsByGrammarSectionOrderAndGrammarId(infoUpdateGrammarSection.getGrammarSectionOrder(), grammar.getId())) {
                    throw new ExistDataException("Grammar Section's order is duplicated");
                }
            }

            if (grammarSectionRepository.existsByTitle(infoUpdateGrammarSection.getTitle())) throw new ExistDataException("Grammar Section Title order is duplicated");

            //Update
            modelMapper.map(infoUpdateGrammarSection, existingGrammarSection);
            existingGrammarSection.setLesson(lesson);
            existingGrammarSection.setGrammar(grammar);

            //Save and return
            GrammarSectionResponse response = modelMapper.map(grammarSectionRepository.save(existingGrammarSection), GrammarSectionResponse.class);
            response.setGrammarId(existingGrammarSection.getGrammar().getId());
            response.setLessonId(existingGrammarSection.getLesson().getId());

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
