package com.busuu.app.dtos.requests.language;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LanguageDTO
{

    @NotBlank(message = "Language name is required!")
    @Size(min = 1, max = 255, message = "Invalid name length (minimum length is 1, maximum length is 255)!")
    private String name;

    @NotBlank(message = "Code is required!")
    private String code;

    @JsonProperty("flag_icon")
    private MultipartFile flagIcon;

}
