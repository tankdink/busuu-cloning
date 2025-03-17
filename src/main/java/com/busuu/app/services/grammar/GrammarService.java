package com.busuu.app.services.grammar;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.grammar.GrammarDTO;
import com.busuu.app.dtos.responses.GrammarResponse;
import com.busuu.app.entities.Grammar;
import com.busuu.app.entities.Language;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.GrammarRepository;
import com.busuu.app.repositories.LanguageRepository;
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
public class GrammarService implements IGrammarService
{

    private final ModelMapper modelMapper;

    private final LanguageRepository languageRepository;

    private final GrammarRepository grammarRepository;

    @Override
    @Transactional
    public GrammarResponse insertGrammar(String requestId, GrammarDTO grammarDTO) 
    {
        try {

            //Check and get exists language
            Language language = languageRepository.findById(grammarDTO.getLanguageId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find language with ID " + grammarDTO.getLanguageId()) );

            //Check exists order
            if (grammarRepository.existsByGrammarOrderAndLanguageId(grammarDTO.getGrammarOrder(), grammarDTO.getLanguageId())) {
                throw new ExistDataException("Grammar's order is duplicated");
            }
            

            //Convert DTO to entity
            Grammar newGrammar = modelMapper.map(grammarDTO, Grammar.class);
            
            //Generate ID for new grammar
            newGrammar.setId(UUID.randomUUID().toString());

            //Set level for new grammar
            newGrammar.setLanguage(language);

            
            //Save, map + add additional properties and return new Grammar
            GrammarResponse savedGrammar = modelMapper.map(grammarRepository.save(newGrammar), GrammarResponse.class);
            savedGrammar.setLanguageId(savedGrammar.getLanguageId());

            return savedGrammar;


        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create new grammar, err= "+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_NEW_GRAMMAR, requestId);
        }
    }

    @Override
    public List<GrammarResponse> getGrammars(String requestId) 
    {
        try {

            //Get all, mapping and return
            return (grammarRepository.findAll()).stream()
                    .map(grammar ->
                    {
                        GrammarResponse response = modelMapper.map(grammar, GrammarResponse.class);
                        
                        response.setLanguageId(grammar.getLanguage().getId());

                        return response;
                    })
                    .collect(Collectors.toList());


        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get grammar list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_ALL_GRAMMAR, requestId);
        }
    }

    @Override
    public GrammarResponse getGrammar(String requestId, String grammarID) 
    {
        try {

            Grammar gettedGrammar = grammarRepository.findById(grammarID)
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find grammar with ID " + grammarID) );


            //Return
            GrammarResponse response = modelMapper.map(gettedGrammar, GrammarResponse.class);
            response.setLanguageId(gettedGrammar.getLanguage().getId());
            
            return response;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get grammar with ID " +  grammarID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_GRAMMAR_BY_ID, requestId);
        }
    }

    @Override
    public List<GrammarResponse> getByLanguageId(String requestId, String languageID)
    {
        try {

            List<Grammar> gettedGrammarList = grammarRepository.findByLanguageId(languageID);
            if (gettedGrammarList.isEmpty()) throw new DataNotFoundException("Cannot find grammar with languageID " + languageID);


            //Return
            return (gettedGrammarList.stream()
                    .map(grammar ->
                    {
                        GrammarResponse response = modelMapper.map(grammar, GrammarResponse.class);

                        response.setLanguageId(grammar.getLanguage().getId());

                        return response;
                    })
                    .collect(Collectors.toList()));


        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get grammar with languageID " +  languageID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_GRAMMAR_BY_LANGUAGE_ID, requestId);
        }
    }

    @Override
    @Transactional
    public GrammarResponse updateGrammar(String requestId, String grammarID, GrammarDTO infoUpdateGrammar) 
    {
        try {

            //Check exists grammar, language/Get update grammar
            Grammar existingGrammar = grammarRepository.findById(grammarID)
                    .orElseThrow( ()-> new DataNotFoundException("No grammar found with ID " + grammarID) );


            if (!Objects.equals(existingGrammar.getGrammarOrder(), infoUpdateGrammar.getGrammarOrder())) {
                if (grammarRepository.existsByGrammarOrderAndLanguageId(infoUpdateGrammar.getGrammarOrder(), existingGrammar.getLanguage().getId())) {
                    throw new ExistDataException("Grammar's order is duplicated");
                }
            }

            Language language = languageRepository.findById(infoUpdateGrammar.getLanguageId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find language with ID " + infoUpdateGrammar.getLanguageId()) );


            //Update
            modelMapper.map(infoUpdateGrammar, existingGrammar);
            existingGrammar.setLanguage(language);

            //Save and return
            GrammarResponse response = modelMapper.map(grammarRepository.save(existingGrammar), GrammarResponse.class);
            response.setLanguageId(existingGrammar.getLanguage().getId());

            return response;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to update grammar with ID " +  grammarID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_GRAMMAR_BY_ID, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteGrammar(String requestId, String grammarID)
    {
        try {

            //Check exist grammar
            Grammar existingGrammar = grammarRepository.findById(grammarID)
                    .orElseThrow( ()-> new DataNotFoundException("No grammar found with ID " + grammarID) );

            grammarRepository.deleteById(grammarID);

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete grammar with ID " +  grammarID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_GRAMMAR_BY_ID, requestId);
        }
    }
}
