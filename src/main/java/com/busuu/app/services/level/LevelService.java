package com.busuu.app.services.level;


import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.level.LevelDTO;
import com.busuu.app.entities.CourseLevel;
import com.busuu.app.entities.Level;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.course.CourseLevelRepository;
import com.busuu.app.repositories.LevelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LevelService implements ILevelService
{

    private final ModelMapper modelMapper;
    private final LevelRepository levelRepository;
    private final CourseLevelRepository courseLevelRepository;

    @Override
    @Transactional
    public Level insertLevel(String requestId, LevelDTO levelDTO)
    {
        try {

            //Check duplicated code, description
            if (levelRepository.existsByCode(levelDTO.getCode())) throw new ExistDataException("Level Code has been used!");
            if (levelRepository.existsByName(levelDTO.getName())) throw new ExistDataException("Level Name has been used!");

            //Convert DTO to entity
            Level newLevel = modelMapper.map(levelDTO, Level.class);

            //Generate ID for new level
            newLevel.setId(UUID.randomUUID().toString());

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

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get level with ID " +  levelID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LEVEL_BY_ID, requestId);
        }
    }

    @Override
    public List<Level> getLevelsByCourseId(String requestId, String courseId)
    {
        try {

            //Get Course - Level list
            //Get each level entity then collect to list
            //Return
            return (courseLevelRepository.findByCourseIdWithSortingLevel(courseId).stream()
                    .map(CourseLevel::getLevel)
                    .collect(Collectors.toList()));

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get level with courseId " +  courseId + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LEVEL_BY_COURSE_ID, requestId);
        }
    }

    @Override
    @Transactional
    public Level updateLevel(String requestId, String levelID, LevelDTO infoUpdateLevel)
    {

        try {

            //Check exists level/Get update level
            Level existingLevel = levelRepository.findById(levelID)
                    .orElseThrow( ()-> new DataNotFoundException("No level found with ID " + levelID) );

            //Check duplicated code, name
            if (!existingLevel.getCode().equals(infoUpdateLevel.getCode())) {
                if ( levelRepository.existsByCode(infoUpdateLevel.getCode()) ) throw new ExistDataException("Level Code has been used!");
            }

            if (!existingLevel.getName().equals(infoUpdateLevel.getName())) {
                if ( levelRepository.existsByName(infoUpdateLevel.getName()) ) throw new ExistDataException("Level Name has been used!");
            }


            modelMapper.map(infoUpdateLevel, existingLevel);

            //Save and return
            return levelRepository.save(existingLevel);

        } catch (Exception e) {
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

            //Check exist level
            Level existingLevel = levelRepository.findById(levelID)
                    .orElseThrow( ()-> new DataNotFoundException("No level found with ID " + levelID) );

            levelRepository.deleteById(levelID);

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete level with ID " +  levelID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_LEVEL_BY_ID, requestId);
        }
    }

    @Override
    public Level getLevelByCode(String requestId, String code) {
        try {
            return levelRepository.findByCode(code);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete level with code " +  code + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LEVEL_BY_CODE, requestId);
        }
    }

}