package com.busuu.app.services.chapter;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.chapter.ChapterDTO;
import com.busuu.app.dtos.responses.ChapterResponse;
import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Course;
import com.busuu.app.entities.Level;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.ChapterProgress;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.ChapterRepository;
import com.busuu.app.repositories.course.CourseRepository;
import com.busuu.app.repositories.LevelRepository;
import com.busuu.app.repositories.progress.ChapterProgressRepository;
import com.busuu.app.repositories.progress.CourseProgressRepository;
import com.busuu.app.specification.ChapterSpecification;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final ChapterProgressRepository chapterProgressRepository;

    @Override
    @Transactional
    public ChapterResponse insertChapter(String requestId, ChapterDTO chapterDTO) 
    {
        try {

            //Check and get exists level
            Level level = levelRepository.findById(chapterDTO.getLevelId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find level with ID " + chapterDTO.getLevelId()) );

//            if (chapterRepository.existsByChapterOrderAndLevelId(chapterDTO.getChapterOrder(), level.getId())) {
//                throw new ExistDataException("Chapter's order is duplicated");
//            }

            //Check exists course, title
            Course course = courseRepository.findById(chapterDTO.getCourseId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find course with ID " + chapterDTO.getCourseId()) );

            if (chapterRepository.existsByTitle(chapterDTO.getTitle())) throw new ExistDataException("Chapter's title is duplicated");

            //Convert DTO to entity
            Chapter newChapter = modelMapper.map(chapterDTO, Chapter.class);


            //Generate ID for new chapter
            newChapter.setId(UUID.randomUUID().toString());

            //Set level for new chapter
            newChapter.setLevel(level);

            //Set course for new chapter
            newChapter.setCourse(course);

            // Set chapter order max + 1
            Integer chapterOrder = chapterRepository.findMaxChapterOrderByLevelIdAndCourseId(chapterDTO.getLevelId(), chapterDTO.getCourseId());
            if (chapterOrder == null) {
                newChapter.setChapterOrder(1);
            } else {
                newChapter.setChapterOrder(chapterOrder + 1);
            }

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
    public Page<ChapterResponse> getChapters(String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, List<String> filterBy, List<String> filterValue)
    {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            //Temp comment, uncomment if use custom repository query, delete later if not use
//            //Pageable - NativeQuery
//            Sort sort = Sort.by(
//                    Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection)),
//                    Sort.Order.by("chapter_id").with(Sort.Direction.fromString(sortDirection))
//            );

            Pageable pageable = PageRequest.of(page, size);

            //Get all, mapping and return
            return (chapterRepository.findAll(ChapterSpecification.getSpecification(searchValue, filterBy, filterValue, sortBy, sortDirection), pageable))
                    .map(chapter ->
                    {
                        ChapterProgress chapterProgress = chapterProgressRepository.findByChapterIdAndUserId(chapter.getId(), userId);
                        ChapterResponse response = modelMapper.map(chapter, ChapterResponse.class);

                        response.setCourseId(chapter.getCourse().getId());
                        response.setLevelId(chapter.getLevel().getId());
                        if (chapterProgress != null) {
                            response.setProgress(chapterProgress.getProgress());
                            response.setIsCompleted(chapterProgress.getIsCompleted());
                        }
                        return response;

                    });


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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            Chapter gettedChapter = chapterRepository.findById(chapterID)
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find chapter with ID " + chapterID) );

            ChapterProgress chapterProgress = chapterProgressRepository.findByChapterIdAndUserId(gettedChapter.getId(), userId);

            //Return
            ChapterResponse response = modelMapper.map(gettedChapter, ChapterResponse.class);
            response.setCourseId(gettedChapter.getCourse().getId());
            response.setLevelId(gettedChapter.getLevel().getId());
            if (chapterProgress != null) {
                response.setProgress(chapterProgress.getProgress());
                response.setIsCompleted(chapterProgress.getIsCompleted());
            }
            return response;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get chapter with ID " +  chapterID + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_CHAPTER_BY_ID, requestId);
        }
    }

    @Override
    public List<ChapterResponse> getByCourseIdAndLevelId(String requestId, String courseID, String levelId)
    {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            List<Chapter> gettedChapterList = chapterRepository.findByCourseIdAndLevelId(courseID, levelId, Sort.by(Sort.Direction.ASC, "chapterOrder"));
            if (gettedChapterList.isEmpty()) throw new DataNotFoundException("There are no chapter found with courseID " + courseID + " and levelID " + levelId);


            //Return
            return (gettedChapterList.stream()
                    .map(chapter ->
                    {
                        ChapterProgress chapterProgress = chapterProgressRepository.findByChapterIdAndUserId(chapter.getId(), userId);

                        ChapterResponse response = modelMapper.map(chapter, ChapterResponse.class);

                        response.setCourseId(chapter.getCourse().getId());
                        response.setLevelId(chapter.getLevel().getId());
                        if (chapterProgress != null) {
                            response.setProgress(chapterProgress.getProgress());
                            response.setIsCompleted(chapterProgress.getIsCompleted());
                        }
                        return response;
                    })
                    .collect(Collectors.toList()));

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get chapter with courseID " +  courseID + " and levelID " + levelId + ", err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_CHAPTER_BY_COURSE_ID_AND_LEVEL_ID, requestId);
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

            //Check exists level, course, title
            Level level = levelRepository.findById(infoUpdateChapter.getLevelId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find level with ID " + infoUpdateChapter.getLevelId()) );

            if (!Objects.equals(existingChapter.getChapterOrder(), infoUpdateChapter.getChapterOrder())) {
                if (chapterRepository.existsByChapterOrderAndLevelId(infoUpdateChapter.getChapterOrder(), level.getId())) {
                    throw new ExistDataException("Chapter's order is duplicated");
                }
            }

            if (!Objects.equals(existingChapter.getTitle(), infoUpdateChapter.getTitle()))
                if (chapterRepository.existsByTitle(infoUpdateChapter.getTitle())) throw new ExistDataException("Chapter's title is duplicated");

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
