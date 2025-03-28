package com.busuu.app.dtos.requests.level;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.validation.constraints.*;




@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LevelDTO
{
    @NotBlank(message = "Level code is required!")
    @Size(min = 1, message = "Invalid Code length (minimum length is 1)!")
    private String code;

    @NotBlank(message = "Level name is required!")
    @Size(min = 1, max = 255, message = "Invalid Name length (minimum length is 1, maximum length is 255)!")
    private String name;

    @NotBlank(message = "Level description is required!")
    @Size(min = 1, message = "Invalid Description length (minimum length is 1)!")
    private String description;
}