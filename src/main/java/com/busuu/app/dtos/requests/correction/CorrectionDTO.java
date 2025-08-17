package com.busuu.app.dtos.requests.correction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CorrectionDTO
{

    @JsonProperty("correction_audio")
    private MultipartFile correctionAudio;

    @JsonProperty("correction_text")
    private String correctionText;

    @JsonProperty("correction_text")
    @NotBlank(message = "Post id which this correction belong to cannot be null")
    private String postId;
}
