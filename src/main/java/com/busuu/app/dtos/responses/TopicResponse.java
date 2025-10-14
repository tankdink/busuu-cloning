package com.busuu.app.dtos.responses;

import com.busuu.app.entities.enums.TopicType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicResponse extends BaseResponse
{
    @JsonProperty("topic_id")
    private String id;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("image_name")
    private String imageName;

    @JsonProperty("video_url")
    private String videoUrl;

    @JsonProperty("video_name")
    private String videoName;

    @JsonProperty("video_description")
    private String videoDescription;

    @JsonProperty("topic_category")
    private String category;

    @JsonProperty("topic_type")
    private TopicType topicType;

    @JsonProperty("header")
    private String header;

    @JsonProperty("topic_question")
    private String topicQuestion;

    @JsonProperty("hint")
    private String hint;

    @JsonProperty("lesson_id")
    private String lessonId;
    
    @JsonProperty("testing")
    @Builder.Default
    private String test = "Testing CI/CD";

}
