package com.busuu.app.dtos.responses;

import com.busuu.app.entities.posts.PostType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse extends BaseResponse
{
    @JsonProperty("post_id")
    private String id;

    @JsonProperty("subject_text")
    private String subjectText;

    @JsonProperty("subject_image_name")
    private String subjectImageName;

    @JsonProperty("subject_image_url")
    private String subjectImageUrl;

    @JsonProperty("subject_video_name")
    private String subjectVideoName;

    @JsonProperty("subject_video_url")
    private String subjectVideoUrl;

    @JsonProperty("post_audio_name")
    private String postAudioName;

    @JsonProperty("post_audio_url")
    private String postAudioUrl;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("post_type")
    private PostType postType;

    @JsonProperty("language_id")
    private String languageId;

    @JsonProperty("correction_count")
    private long correctionCount;


}
