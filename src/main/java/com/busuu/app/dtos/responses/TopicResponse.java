package com.busuu.app.dtos.responses;

import com.busuu.app.entities.topic.TopicType;
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

    @JsonProperty("video_category")
    private String category;

    @JsonProperty("topic_type")
    private TopicType topicType;

}
