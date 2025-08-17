package com.busuu.app.dtos.requests.post;

import com.busuu.app.entities.post.PostType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO
{

    @JsonProperty("post_type")
    @NotBlank(message = "Post type cannot be null")
    @Pattern(regexp = "TEXT|AUDIO", message = "Invalid post type. Must be 'TEXT' or 'AUDIO' (uppercase required).")
    private String postTypes;

    @JsonProperty("subject_text")
    @NotBlank(message = "Subject text cannot be null")
    private String subjectText;

    @JsonProperty("subject_img")
    private MultipartFile subjectImg;

    @JsonProperty("subject_video")
    private MultipartFile subjectVideo;

    @JsonProperty("post_audio")
    private MultipartFile postAudio;

    @JsonProperty("post_text")
    private String postText;

}
