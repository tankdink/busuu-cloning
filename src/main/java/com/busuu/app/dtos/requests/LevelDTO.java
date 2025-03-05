package com.busuu.app.dtos.requests;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LevelDTO
{
    @NotBlank(message = "Level code is required!")
    @Size(min = 1, message = "Invalid ID length (minimum length is 1)!")
    private String code;

    @NotBlank(message = "Level name is required!")
    @Size(min = 1, message = "Invalid ID length (minimum length is 1)!")
    private String name;

    @NotBlank(message = "Level description is required!")
    @Size(min = 1, message = "Invalid ID length (minimum length is 1)!")
    private String description;
}