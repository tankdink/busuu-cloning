package com.busuu.app.services.level;


import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.LevelDTO;
import com.busuu.app.entities.Level;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.LevelRepository;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LevelService implements ILevelService
{

    private final ModelMapper modelMapper;
    private final LocalizationUtils localizationUtils;
    private final LevelRepository levelRepository;

    @Override
    @Transactional
    public Level insertLevel(String requestId, LevelDTO levelDTO)
    {
        try {

            //Convert DTO to entity
            Level newLevel = modelMapper.map(levelDTO, Level.class);

            //Generate ID for new level
            newLevel.setId(UUID.randomUUID().toString());

            //Check duplicated id, code, description
            if (levelRepository.existsById(newLevel.getId())) throw new ExistDataException("Level ID has been used!");
            if (levelRepository.existsByCode(newLevel.getCode())) throw new ExistDataException("Level Code has been used!");
            if (levelRepository.existsByName(newLevel.getName())) throw new ExistDataException("Level Name has been used!");

            //Save and return new Level
            return levelRepository.save(newLevel);

        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create new level, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_NEW_LEVEL, requestId);
        }

    }

    @Override
    public List<Level> getLevels(String requestId)
    {
        try {

            //Get all Level with Default JPA repository and return
            return levelRepository.findAll();

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get level list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_ALL_LEVEL, requestId);
        }

    }

    @Override
    public Level getLevel(String requestId, String levelID)
    {
        try {

            return levelRepository.findById(levelID)
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find level with ID " + levelID) );

        } catch (DataNotFoundException e) {
            log.error("requestId="+requestId+",failed to get level with ID " +  levelID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LEVEL_BY_ID, requestId);
        }
    }

    @Override
    @Transactional
    public Level updateLevel(String requestId, String levelID, LevelDTO infoUpdateLevel)
    {

        try {

            //Check exists level/Get update level
            Level getModifyLevel = levelRepository.findById(levelID)
                    .orElseThrow( ()-> new DataNotFoundException("No level found with ID " + levelID) );

            //Check duplicated code, name
            if (!Objects.equals(getModifyLevel.getCode(), infoUpdateLevel.getCode()))
                if ( levelRepository.existsByCode(infoUpdateLevel.getCode()) ) throw new ExistDataException("Level Code has been used!");

            if (!Objects.equals(getModifyLevel.getName(), infoUpdateLevel.getName()))
                if ( levelRepository.existsByName(infoUpdateLevel.getName()) ) throw new ExistDataException("Level Name has been used!");


            //Convert
            modelMapper.map(infoUpdateLevel, getModifyLevel);

            //Save and return
            return levelRepository.save(getModifyLevel);

        } catch (DataNotFoundException e) {
            log.error("requestId="+requestId+",failed to update level with ID " +  levelID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_LEVEL_BY_ID, requestId);
        }

    }

    @Override
    @Transactional
    public void deleteLevel(String requestId, String levelID)
    {
        try {

            Level gettedLevel = levelRepository.findById(levelID)
                    .orElseThrow( ()-> new DataNotFoundException("No level found with ID " + levelID) );

            levelRepository.deleteById(levelID);

        } catch (DataNotFoundException e) {
            log.error("requestId="+requestId+",failed to delete level with ID " +  levelID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_LEVEL_BY_ID, requestId);
        }
    }

}