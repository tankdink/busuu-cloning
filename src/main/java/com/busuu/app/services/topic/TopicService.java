package com.busuu.app.services.topic;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.TopicResponse;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.topics.Topic;
import com.busuu.app.entities.topics.TopicType;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.LessonRepository;
import com.busuu.app.repositories.TopicRepository;
import com.busuu.app.specification.TopicSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService implements ITopicService
{

    private final TopicRepository topicRepository;

    private final LessonRepository lessonRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<TopicResponse> getTopicsByType(String requestId, String topicType, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, String topicCategory) throws DataNotFoundException
    {
        try {

            try {

                TopicType type;

                type = TopicType.valueOf(topicType.toUpperCase());

            } catch (Exception e)
            {
                throw new IllegalArgumentException("Illegal topic type! Must be \"IMAGE\" or \"VIDEO\" (ignore case) ");
            }

            //Pageable pageable = PageRequest.of(page, size);

            Specification<Topic> spec = TopicSpecification.getSpecification(topicType, searchValue, topicCategory, sortBy, sortDirection);
            List<TopicResponse> responseList = topicRepository.findAll(spec).stream().map(
                    topic ->
                    {
                        TopicResponse response = modelMapper.map(topic, TopicResponse.class);

                        if (topic.getTopicCategory() != null) {
                            response.setCategory(topic.getTopicCategory().getCategoryName());
                        }
                        else response.setCategory(null);
                        if (topic.getLesson() != null) response.setLessonId(topic.getLesson().getId());

                        return response;
                    }
            ).toList(); //This list is unmodifiable

            return randomize(responseList);


        } catch (Exception e) {
            log.error("requestId=" + requestId + ",failed to get topics by type, err=" + e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_TOPIC, requestId);
        }

    }

    @Override
    public List<TopicResponse> getTopicsByLessonId(String requestId, String lessonId) {
        try {

            boolean existingLesson = lessonRepository.existsById(lessonId);

            if (!existingLesson) throw new DataNotFoundException("No lesson found with id " + lessonId);


            return topicRepository.findByLessonId(lessonId).stream().map(
                    topic ->
                    {
                        TopicResponse response = modelMapper.map(topic, TopicResponse.class);

                        if (topic.getTopicCategory() != null) {
                            response.setCategory(topic.getTopicCategory().getCategoryName());
                        }
                        else response.setCategory(null);
                        if (topic.getLesson() != null) response.setLessonId(topic.getLesson().getId());

                        return response;
                    }
            ).toList();


        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get topic, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_COURSE, requestId);
        }
    }

    @Override
    public TopicResponse getTopic(String requestId, String topicId)
    {
        try {

            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find topic with ID = " + topicId));

            TopicResponse response = modelMapper.map(topic, TopicResponse.class);

            if (topic.getTopicCategory() != null) {
                response.setCategory(topic.getTopicCategory().getCategoryName());
            }
            else response.setCategory(null);
            if (topic.getLesson() != null) response.setLessonId(topic.getLesson().getId());

            return response;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get topic, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_COURSE, requestId);
        }
    }

    public List<TopicResponse> randomize(List<TopicResponse> list)
    {

        if (list.size() <= 5) {
            return list;
        }

        //Response list
        List<TopicResponse> resultList = new ArrayList<>(5);

        //Shuffle the copy list
        List<TopicResponse> copy = new ArrayList<>(list);
        Collections.shuffle(copy);

        //Add the first 5 elements of the shuffled list to result list
        for (int i = 0; i < 5; i++) resultList.add(copy.get(i));

        return resultList;

    }
}
