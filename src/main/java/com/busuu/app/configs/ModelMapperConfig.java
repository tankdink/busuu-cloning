package com.busuu.app.configs;

import com.busuu.app.dtos.responses.ChapterResponse;
import com.busuu.app.entities.Chapter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper () {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

//        modelMapper.typeMap(Chapter.class, ChapterResponse.class)
//                .addMappings(mapper -> {
//                    mapper.map(src -> src.getCourse().getId(), ChapterResponse::setCourse_id);
//                    mapper.map(src -> src.getLevel().getId(), ChapterResponse::setLevel_id);
//                });



        return modelMapper;
    }



}