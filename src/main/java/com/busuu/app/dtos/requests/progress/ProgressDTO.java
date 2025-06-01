package com.busuu.app.dtos.requests.progress;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgressDTO {

    @JsonProperty("user_id")
    @NotBlank(message = "User's ID is required")
    private String userId;

    @JsonProperty("object_id")
    @NotBlank(message = "Object's ID is required")
    private String objectId;
}
