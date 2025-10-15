package com.busuu.app.services;

import com.busuu.app.dtos.responses.TopicResponse;
import com.busuu.app.entities.topics.Topic;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.TopicRepository;
import com.busuu.app.services.topic.TopicService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Test the getTopic function only

@ExtendWith(MockitoExtension.class) //For services
class TopicServiceTest {

    //Mock dependencies

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private ModelMapper modelMapper;

    //Real TopicService, mocks above instead of real beans.
    @InjectMocks
    private TopicService topicService;

    //Return if data was found
    @Test
    void getTopic_shouldReturnMappedResponse()
    {

        //Create dummy topic entity for testing
        Topic topic = new Topic();
        topic.setId("T001");


        //Stimulate the work flow of services: call service -> find in repo (1) -> map (2) -> return mapped response (3)

        //When topicRepository.findById("T001") is called, return this topic (1)
        when(topicRepository.findById("T001")).thenReturn(Optional.of(topic));

        //Fake response should come from ModelMapper (prepare for mapping)
        TopicResponse mappedResponse = new TopicResponse();
        mappedResponse.setId("T001Mapped");

        //Mock the modelMapper behavior to return mappedResponse (2) + (3)
        when(modelMapper.map(topic, TopicResponse.class)).thenReturn(mappedResponse);


        //Now real test

        //Call the service
        TopicResponse result = topicService.getTopic("req1", "T001");

        //Verify the result is correct
        assertNotNull(result); //Ensure not null
        assertEquals("T001Mapped", result.getId()); //Ensure mapping worked

        //Verify the mocks were called
        verify(topicRepository).findById("T001");
        verify(modelMapper).map(topic, TopicResponse.class);

    }

    @Test
    void getTopic_shouldThrowException_whenNotFound()
    {

        //Arrange
        when(topicRepository.findById("no_id")).thenReturn(Optional.empty());

        //Call for testing
        ErrorHandleException ex = assertThrows(
                ErrorHandleException.class,
                () -> topicService.getTopic("req2", "no_id")
        );

        assertEquals("Cannot find topic with ID = no_id", ex.getMessage());
    }
}
