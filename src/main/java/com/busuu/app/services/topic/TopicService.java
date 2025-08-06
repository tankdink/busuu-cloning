package com.busuu.app.services.topic;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.TopicResponse;
import com.busuu.app.entities.topic.Topic;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService implements ITopicService
{

    private final TopicRepository topicRepository;

    private final ModelMapper modelMapper;

    @Override
    public Page<TopicResponse> getTopicsByType(String requestId, String topicType, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, List<String> filterBy, List<String> filterValue) throws DataNotFoundException
    {
        try {

            Pageable pageable = PageRequest.of(page, size);

            Specification<Topic> spec = TopicSpecification.getSpecification(topicType, searchValue, filterBy, filterValue, sortBy, sortDirection);
            return topicRepository.findAll(spec, pageable).map(
                    topic ->
                    {
                        TopicResponse response = modelMapper.map(topic, TopicResponse.class);

                        if (topic.getVideoCategory() != null) {
                            response.setCategory(topic.getVideoCategory().getCategoryName());
                        }
                        else response.setCategory(null);

                        return response;
                    }
            );


        } catch (Exception e) {
            log.error("requestId=" + requestId + ",failed to get topics by type, err=" + e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_TOPIC, requestId);
        }

    }
}
