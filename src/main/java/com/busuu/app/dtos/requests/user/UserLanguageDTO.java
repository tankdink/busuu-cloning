package com.busuu.app.dtos.requests.user;

import com.busuu.app.entities.enums.LearningStatus;
import com.busuu.app.entities.enums.SpeakingStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLanguageDTO {

    @JsonProperty("language_id")
    @NotBlank(message = "Language ID cannot be null or empty")
    private String languageId;

    @JsonProperty("learning_status")
    private LearningStatus learningStatus;

    @JsonProperty("speaking_status")
    private SpeakingStatus speakingStatus;

    @JsonProperty("user_id")
    @NotBlank(message = "User ID cannot be null or empty")
    private String userId;

    @JsonProperty("is_learning")
    @NotNull(message = "Is learning cannot be null")
    private Boolean isLearning;
}
