package com.busuu.app.services.grammar;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.grammar.GrammarDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.GrammarResponse;
import com.busuu.app.entities.Grammar;
import com.busuu.app.entities.Language;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.grammar.GrammarRepository;
import com.busuu.app.repositories.LanguageRepository;
import com.busuu.app.services.cloudinary.IUploadCloudinaryService;
import com.busuu.app.utils.UploadCloudinaryUtil;
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
import org.springframework.web.multipart.MultipartFile;

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

    private final IUploadCloudinaryService uploadCloudinaryService;

    @Override
    @Transactional
    public GrammarResponse insertGrammar(String requestId, GrammarDTO grammarDTO) 
    {
        try {

            //Check and get exists language
            Language language = languageRepository.findById(grammarDTO.getLanguageId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find language with ID " + grammarDTO.getLanguageId()) );

            //Check exists order, title
//            if (grammarRepository.existsByGrammarOrderAndLanguageId(grammarDTO.getGrammarOrder(), grammarDTO.getLanguageId())) {
//                throw new ExistDataException("Grammar's order is duplicated");
//            }

            if (grammarRepository.existsByTitle(grammarDTO.getTitle())) throw new ExistDataException("Grammar's title is duplicated");

            //Image process
            CloudinaryResponse cloudinaryResponse = null;
            if (grammarDTO.getFlagIcon() != null) {
                cloudinaryResponse = uploadFlagIcon(grammarDTO.getFlagIcon());
            }

            //Convert DTO to entity
            Grammar newGrammar = modelMapper.map(grammarDTO, Grammar.class);
            
            //Generate ID for new grammar
            newGrammar.setId(UUID.randomUUID().toString());

            //Set level for new grammar
            newGrammar.setLanguage(language);

            // Set grammar order max + 1
            Integer grammarOrder = grammarRepository.findMaxGrammarOrderByLanguageId(grammarDTO.getLanguageId());
            if (grammarOrder == null) {
                newGrammar.setGrammarOrder(1);
            } else {
                newGrammar.setGrammarOrder(grammarOrder + 1);
            }

            //Set cloudinary info to new grammar if exist
            if (cloudinaryResponse != null) {
                newGrammar.setFlagIconUrl(cloudinaryResponse.getUrl());
                newGrammar.setFlagIconName(cloudinaryResponse.getPublicId());
            }

            //Save, map + add additional properties and return new Grammar
            GrammarResponse savedGrammar = modelMapper.map(grammarRepository.save(newGrammar), GrammarResponse.class);
            savedGrammar.setLanguageId(newGrammar.getLanguage().getId());

            return savedGrammar;


        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create new grammar, err= "+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_NEW_GRAMMAR, requestId);
        }
    }

    @Override
    public Page<GrammarResponse> getGrammars(String requestId, int page, int size, String sortBy, String sortDirection)
    {
        try {

            //Pageable
            Sort sort = Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection)));
            Pageable pageable = PageRequest.of(page, size, sort);

            //Get all, mapping and return
            return (grammarRepository.findAll(pageable))
                    .map(grammar ->
                    {
                        GrammarResponse response = modelMapper.map(grammar, GrammarResponse.class);
                        
                        response.setLanguageId(grammar.getLanguage().getId());

                        return response;
                    });


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
            if (gettedGrammarList.isEmpty()) throw new DataNotFoundException("No grammar found with languageID " + languageID);


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

            //Check exists grammar, title, language/Get update grammar
            Grammar existingGrammar = grammarRepository.findById(grammarID)
                    .orElseThrow( ()-> new DataNotFoundException("No grammar found with ID " + grammarID) );


            if (!Objects.equals(existingGrammar.getGrammarOrder(), infoUpdateGrammar.getGrammarOrder())) {
                if (grammarRepository.existsByGrammarOrderAndLanguageId(infoUpdateGrammar.getGrammarOrder(), existingGrammar.getLanguage().getId())) {
                    throw new ExistDataException("Grammar's order is duplicated");
                }
            }

            Language language = languageRepository.findById(infoUpdateGrammar.getLanguageId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find language with ID " + infoUpdateGrammar.getLanguageId()) );

            if (!Objects.equals(existingGrammar.getTitle(), infoUpdateGrammar.getTitle()))
                if (grammarRepository.existsByTitle(infoUpdateGrammar.getTitle())) throw new ExistDataException("Grammar's title is duplicated");

            //Image process
            if (infoUpdateGrammar.getFlagIcon() != null) {
                boolean isRemove = true;
                if (existingGrammar.getFlagIconName()!= null) {
                    isRemove = uploadCloudinaryService.removeFile(existingGrammar.getFlagIconName());
                }

                if (isRemove) {
                    CloudinaryResponse cloudinaryResponse = uploadFlagIcon(infoUpdateGrammar.getFlagIcon());
                    if (cloudinaryResponse != null) {
                        existingGrammar.setFlagIconUrl(cloudinaryResponse.getUrl());
                        existingGrammar.setFlagIconName(cloudinaryResponse.getPublicId());
                    }
                }
            }

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

            //Image process
            if (existingGrammar.getFlagIconName() != null) uploadCloudinaryService.removeFile(existingGrammar.getFlagIconName());

            grammarRepository.deleteById(grammarID);

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete grammar with ID " +  grammarID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_GRAMMAR_BY_ID, requestId);
        }
    }

    private CloudinaryResponse uploadFlagIcon(MultipartFile file) throws Exception {
        UploadCloudinaryUtil.assertAllowed(file, "image");
        String fileName = UploadCloudinaryUtil.getFileName(file.getOriginalFilename());
        CloudinaryResponse response = uploadCloudinaryService.uploadFile(file, fileName, "image");
        return response;
    }
}
