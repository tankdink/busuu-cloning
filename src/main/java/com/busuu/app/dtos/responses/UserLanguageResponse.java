package com.busuu.app.dtos.responses;

import com.busuu.app.entities.enums.LearningStatus;
import com.busuu.app.entities.enums.SpeakingStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLanguageResponse extends BaseResponse {

    private String id;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("language_id")
    private String languageId;

    @JsonProperty("learning_status")
    private LearningStatus learningStatus;

    @JsonProperty("speaking_status")
    private SpeakingStatus speakingStatus;

    @JsonProperty("date_started")
    @JsonIgnore
    private Date dateStarted;

    @JsonProperty("date_completed")
    @JsonIgnore
    private Date dateCompleted;

    @JsonProperty("is_learning")
    private Boolean isLearning;
}
