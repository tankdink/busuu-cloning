package com.busuu.app.services.level;


import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.level.LevelDTO;
import com.busuu.app.dtos.responses.LevelResponse;
import com.busuu.app.entities.CourseLevel;
import com.busuu.app.entities.Level;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.LevelProgress;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.course.CourseLevelRepository;
import com.busuu.app.repositories.LevelRepository;
import com.busuu.app.repositories.progress.LevelProgressRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final LevelProgressRepository levelProgressRepository;

    @Override
    @Transactional
    public LevelResponse insertLevel(String requestId, LevelDTO levelDTO)
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
            newLevel = levelRepository.save(newLevel);

            return modelMapper.map(newLevel, LevelResponse.class);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create new level, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_NEW_LEVEL, requestId);
        }

    }

    @Override
    public List<LevelResponse> getLevels(String requestId)
    {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            return levelRepository.findAll().stream().map(
                    level -> {
                        LevelProgress levelProgress = levelProgressRepository.findByLevelIdAndUserId(level.getId(), userId);

                        LevelResponse levelResponse = modelMapper.map(level, LevelResponse.class);

                        if (levelProgress != null) {
                            levelResponse.setIsCompleted(levelProgress.getIsCompleted());
                            levelResponse.setProgress(levelProgress.getProgress());
                        }
                        return levelResponse;
                    }).toList();
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get level list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_ALL_LEVEL, requestId);
        }

    }

    @Override
    public LevelResponse getLevel(String requestId, String levelID)
    {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            Level existingLevel = levelRepository.findById(levelID)
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find level with ID " + levelID) );

            LevelProgress levelProgress = levelProgressRepository.findByLevelIdAndUserId(existingLevel.getId(), userId);

            LevelResponse levelResponse =  modelMapper.map(existingLevel, LevelResponse.class);
            if (levelProgress != null) {
                levelResponse.setIsCompleted(levelProgress.getIsCompleted());
                levelResponse.setProgress(levelProgress.getProgress());
            }
            return levelResponse;
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get level with ID " +  levelID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LEVEL_BY_ID, requestId);
        }
    }

    @Override
    public List<LevelResponse> getLevelsByCourseId(String requestId, String courseId)
    {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            //Get Course - Level list
            //Get each level entity then collect to list
            //Return
            List<Level> levels = (courseLevelRepository.findByCourseIdWithSortingLevel(courseId).stream()
                    .map(CourseLevel::getLevel)
                    .toList());

            return levels.stream().map(
                    level -> {
                        LevelProgress levelProgress = levelProgressRepository.findByLevelIdAndUserId(level.getId(), userId);

                        LevelResponse levelResponse = modelMapper.map(level, LevelResponse.class);
                        if (levelProgress != null) {
                            levelResponse.setIsCompleted(levelProgress.getIsCompleted());
                            levelResponse.setProgress(levelProgress.getProgress());
                        }
                        return levelResponse;
                    }
            ).toList();

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get level with courseId " +  courseId + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LEVEL_BY_COURSE_ID, requestId);
        }
    }

    @Override
    @Transactional
    public LevelResponse updateLevel(String requestId, String levelID, LevelDTO infoUpdateLevel)
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
            existingLevel = levelRepository.save(existingLevel);

            return modelMapper.map(existingLevel, LevelResponse.class);
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
    public LevelResponse getLevelByCode(String requestId, String code) {
        try {
            return modelMapper.map(levelRepository.findByCode(code), LevelResponse.class);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete level with code " +  code + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LEVEL_BY_CODE, requestId);
        }
    }

}