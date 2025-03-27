package com.busuu.app.services.chapter;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.chapter.ChapterDTO;
import com.busuu.app.dtos.responses.ChapterResponse;
import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Course;
import com.busuu.app.entities.Level;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.ChapterRepository;
import com.busuu.app.repositories.CourseRepository;
import com.busuu.app.repositories.LevelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterService implements IChapterService
{

    private final ModelMapper modelMapper;

    private final LevelRepository levelRepository;

    private final CourseRepository courseRepository;

    private final ChapterRepository chapterRepository;


    @Override
    @Transactional
    public ChapterResponse insertChapter(String requestId, ChapterDTO chapterDTO) 
    {
        try {

            //Check and get exists level
            Level level = levelRepository.findById(chapterDTO.getLevelId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find level with ID " + chapterDTO.getLevelId()) );

            if (chapterRepository.existsByChapterOrderAndLevelId(chapterDTO.getChapterOrder(), level.getId())) {
                throw new ExistDataException("Chapter's order is duplicated");
            }

            //Check exists course
            Course course = courseRepository.findById(chapterDTO.getCourseId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find course with ID " + chapterDTO.getCourseId()) );


            //Convert DTO to entity
            Chapter newChapter = modelMapper.map(chapterDTO, Chapter.class);


            //Generate ID for new chapter
            newChapter.setId(UUID.randomUUID().toString());

            //Set level for new chapter
            newChapter.setLevel(level);

            //Set course for new chapter
            newChapter.setCourse(course);


            //Save, map + add additional properties and return new Chapter
            ChapterResponse savedChapter = modelMapper.map(chapterRepository.save(newChapter), ChapterResponse.class);
            savedChapter.setCourseId(course.getId());
            savedChapter.setLevelId(level.getId());

            return savedChapter;


        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create new chapter, err= "+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_NEW_CHAPTER, requestId);
        }
    }

    @Override
    public List<ChapterResponse> getChapters(String requestId) 
    {
        try {
            
            //Get all, mapping and return
            return (chapterRepository.findAll()).stream()
                    .map(chapter ->
                    {
                        ChapterResponse response = modelMapper.map(chapter, ChapterResponse.class);

                        response.setCourseId(chapter.getCourse().getId());
                        response.setLevelId(chapter.getLevel().getId());

                        return response;
                    })
                    .collect(Collectors.toList());
            

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get chapter list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_ALL_CHAPTER, requestId);
        }
    }

    @Override
    public ChapterResponse getChapter(String requestId, String chapterID) 
    {
        try {

            Chapter gettedChapter = chapterRepository.findById(chapterID)
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find chapter with ID " + chapterID) );


            //Return
            ChapterResponse response = modelMapper.map(gettedChapter, ChapterResponse.class);
            response.setCourseId(gettedChapter.getCourse().getId());
            response.setLevelId(gettedChapter.getLevel().getId());
            return response;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get chapter with ID " +  chapterID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_CHAPTER_BY_ID, requestId);
        }
    }

    @Override
    @Transactional
    public ChapterResponse updateChapter(String requestId, String chapterID, ChapterDTO infoUpdateChapter) 
    {
        try {

            //Check exists chapter/Get update chapter
            Chapter existingChapter = chapterRepository.findById(chapterID)
                    .orElseThrow( ()-> new DataNotFoundException("No chapter found with ID " + chapterID) );

            //Check exists level, course
            Level level = levelRepository.findById(infoUpdateChapter.getLevelId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find level with ID " + infoUpdateChapter.getLevelId()) );

            if (!Objects.equals(existingChapter.getChapterOrder(), infoUpdateChapter.getChapterOrder())) {
                if (chapterRepository.existsByChapterOrderAndLevelId(infoUpdateChapter.getChapterOrder(), level.getId())) {
                    throw new ExistDataException("Chapter's order is duplicated");
                }
            }

            Course course = courseRepository.findById(infoUpdateChapter.getCourseId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find course with ID " + infoUpdateChapter.getCourseId()) );



            //Update
            modelMapper.map(infoUpdateChapter, existingChapter);
            existingChapter.setLevel(level);
            existingChapter.setCourse(course);

            //Save and return
            ChapterResponse response = modelMapper.map(chapterRepository.save(existingChapter), ChapterResponse.class);
            response.setCourseId(existingChapter.getCourse().getId());
            response.setLevelId(existingChapter.getLevel().getId());

            return response;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to update chapter with ID " +  chapterID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_CHAPTER_BY_ID, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteChapter(String requestId, String chapterID) 
    {
        try {

            //Check exist chapter
            Chapter existingChapter = chapterRepository.findById(chapterID)
                    .orElseThrow( ()-> new DataNotFoundException("No chapter found with ID " + chapterID) );

            chapterRepository.deleteById(chapterID);

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete chapter with ID " +  chapterID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_CHAPTER_BY_ID, requestId);
        }
    }
}
