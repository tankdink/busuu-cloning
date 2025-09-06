package com.busuu.app.services.topicCategory;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.entities.topics.TopicCategory;
import com.busuu.app.entities.topics.TopicType;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.TopicCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicCategoryService implements ITopicCategoryService
{

    private final TopicCategoryRepository topicCategoryRepository;

    @Override
    public List<TopicCategory> getTopicCategories(String requestId, String topicType)
    {
        try {

            if (topicType == null) return topicCategoryRepository.findAll();
            else
            {
                try {

                    TopicType type;

                    type = TopicType.valueOf(topicType.toUpperCase());

                } catch (Exception e)
                {
                    throw new IllegalArgumentException("Illegal topic type! Must be \"IMAGE\" or \"VIDEO\" (ignore case) ");
                }

                return topicCategoryRepository.findByTopicType(TopicType.valueOf(topicType.toUpperCase()));
            }

        } catch (Exception e) {
            log.error("requestId=" + requestId + ",failed to get topics category list, err=" + e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_TOPIC_CATEGORY, requestId);
        }
    }
}
