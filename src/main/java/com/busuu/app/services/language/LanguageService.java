package com.busuu.app.services.language;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.chapter.ChapterDTO;
import com.busuu.app.dtos.requests.language.LanguageDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.LanguageResponse;
import com.busuu.app.entities.Language;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.LanguageRepository;
import com.busuu.app.services.cloudinary.IUploadCloudinaryService;
import com.busuu.app.utils.UploadCloudinaryUtil;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LanguageService implements ILanguageService
{

    private final LanguageRepository languageRepository;

    private final IUploadCloudinaryService uploadCloudinaryService;

    private final ModelMapper modelMapper;

    @Override
    public List<LanguageResponse> getAllWithTotalUsersLearning (String requestId) {
        try {
            return languageRepository.findAllWithTotalUsersLearning();
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get language list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_ALL_LANGUAGE, requestId);
        }
    }

    @Override
    @Transactional
    public LanguageResponse insertLanguage(String requestId, LanguageDTO languageDTO)
    {
        try {
            if (languageRepository.existsByName(languageDTO.getName())) {
                throw new ExistDataException("Language's name is duplicated");
            }

            //Image
            CloudinaryResponse cloudinaryResponse = null;
            if (languageDTO.getFlagIcon() != null) {
                cloudinaryResponse = uploadFlagIcon(languageDTO.getFlagIcon());
            }


            //Mapping, setting and save
            Language newLanguage = modelMapper.map(languageDTO, Language.class);
            newLanguage.setId(UUID.randomUUID().toString());
            if (cloudinaryResponse != null) {
                newLanguage.setFlagIconUrl(cloudinaryResponse.getUrl());
                newLanguage.setFlagIconName(cloudinaryResponse.getPublicId());
            }

            return modelMapper.map(languageRepository.save(newLanguage), LanguageResponse.class);

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to create new language, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_NEW_LANGUAGE, requestId);
        }
    }

    @Override
    public Page<LanguageResponse> getLanguages(String requestId, int page, int size, String sortBy, String sortDirection)
    {
        try {

            //Pageable - Non-native
            Sort sort = Sort.by(
                    Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection)),
                    Sort.Order.by("id").with(Sort.Direction.fromString(sortDirection))
            );
            Pageable pageable = PageRequest.of(page, size, sort);

            //Get all and return
            return (languageRepository.findAll(pageable))
                    .map(language ->
                    {
                        return modelMapper.map(language, LanguageResponse.class);

                    });


        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get language list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_ALL_LANGUAGE, requestId);
        }
    }

    @Override
    public LanguageResponse getLanguage(String requestId, String languageID)
    {
        try {

            Language gettedLanguage = languageRepository.findById(languageID)
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find language with ID " + languageID) );

            return modelMapper.map(gettedLanguage, LanguageResponse.class);


        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get language with ID " +  languageID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LANGUAGE_BY_ID, requestId);
        }
    }

    @Override
    @Transactional
    public LanguageResponse updateLanguage(String requestId, String languageId, LanguageDTO infoUpdateLanguage)
    {
        try {

            Language existingLanguage = languageRepository.findById(languageId)
                    .orElseThrow( ()-> new DataNotFoundException("No language found with ID " + languageId) );

            if (!existingLanguage.getName().equals(infoUpdateLanguage.getName()))
            {
                if (languageRepository.existsByName(infoUpdateLanguage.getName())) throw new ExistDataException("Language Name has been used!");
            }

            //Image
            if (infoUpdateLanguage.getFlagIcon() != null) {
                boolean isRemove = true;
                if (existingLanguage.getFlagIconName()!= null) {
                    isRemove = uploadCloudinaryService.removeFile(existingLanguage.getFlagIconName());
                }

                if (isRemove) {
                    CloudinaryResponse cloudinaryResponse = uploadFlagIcon(infoUpdateLanguage.getFlagIcon());
                    if (cloudinaryResponse != null) {
                        existingLanguage.setFlagIconUrl(cloudinaryResponse.getUrl());
                        existingLanguage.setFlagIconName(cloudinaryResponse.getPublicId());
                    }
                }
            }


            //Mapping, setting and save
            modelMapper.map(infoUpdateLanguage, existingLanguage);

            return  modelMapper.map(languageRepository.save(existingLanguage), LanguageResponse.class);

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to update language, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_LANGUAGE_BY_ID, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteLanguage(String requestId, String languageId)
    {
        try {
            Language existingLanguage = languageRepository.findById(languageId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Language with ID = " + languageId));

            if (existingLanguage.getFlagIconName() != null) uploadCloudinaryService.removeFile(existingLanguage.getFlagIconName());

            languageRepository.deleteById(languageId);

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete language, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_LANGUAGE_BY_ID, requestId);
        }
    }

    private CloudinaryResponse uploadFlagIcon(MultipartFile file) throws Exception
    {
        UploadCloudinaryUtil.assertAllowed(file, "image");
        String fileName = UploadCloudinaryUtil.getFileName(file.getOriginalFilename());
        return uploadCloudinaryService.uploadFile(file, fileName, "image");
    }
}
