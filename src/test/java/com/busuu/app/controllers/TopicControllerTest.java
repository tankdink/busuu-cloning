package com.busuu.app.controllers;

import com.busuu.app.dtos.responses.TopicResponse;
import com.busuu.app.services.topic.ITopicService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//Test get topic by id (success) and get all with type (failed)
@WebMvcTest(controllers = TopicController.class)
@Import(TopicControllerTest.MockSecurityConfig.class) //Fake security beans
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc; //Fake HTTP requests (no real server)

    @MockBean //Mock Spring beans for dependencies.
    private ITopicService topicService;

    @MockBean
    private LocalizationUtils localizationUtils;

    @MockBean
    private com.busuu.app.components.JwtTokenUtil jwtTokenUtil; //Fake jwt

    @TestConfiguration
    static class MockSecurityConfig
    {

        @Bean
        public UserDetailsService userDetailsService()
        {

            //Fake user for security context
            return username -> new User(
                    "testing",
                    "",
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

        }
    }

    @Test
    @WithMockUser(username = "testing", roles = {"USER"})
    void getTopic_shouldReturnOkResponse() throws Exception
    {

        //Just like services testing, stimulate the progress

        //Fake a response
        TopicResponse mockTopic = new TopicResponse();
        mockTopic.setId("abc");
        mockTopic.setHeader("Sample topic header");

        //When controller calls service.getTopic(...) return mockTopic (fake response)
        when(topicService.getTopic(anyString(), eq("abc"))).thenReturn(mockTopic);

        //Mock localization message
        when(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                .thenReturn("Success");

        //Jwt process, remove if no need security
        when(jwtTokenUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);
        when(jwtTokenUtil.extractEmail(anyString())).thenReturn("testing");


        //Call for testing (fake request), check for response base on real return
        mockMvc.perform(get("/topics/{id}", "abc")
                        .header("Authorization", "Bearer fake"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.topic_id").value("abc"))
                .andExpect(jsonPath("$.data.header").value("Sample topic header"))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));


    }

    @Test
    @WithMockUser(username = "testing", roles = {"USER"})
    void getTopics_shouldHandleException() throws Exception
    {

        //Stimulate
        when(topicService.getTopicsByType(anyString(), anyString(), anyInt(), anyInt(), anyList(), anyList(), any(), any()))
                .thenThrow(new RuntimeException("Unexpected error"));
        when(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                .thenReturn("Failed");

        //Jwt process, remove if no need security
        when(jwtTokenUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);
        when(jwtTokenUtil.extractEmail(anyString())).thenReturn("testing");

        //Call
        mockMvc.perform(get("/topic").param("topic-type", "IMAGE")
                        .header("Authorization", "Bearer fake"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}

