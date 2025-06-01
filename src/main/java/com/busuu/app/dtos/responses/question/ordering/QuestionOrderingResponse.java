package com.busuu.app.dtos.responses.question.ordering;

import com.busuu.app.dtos.responses.question.QuestionResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionOrderingResponse extends QuestionResponse {

    @JsonProperty("parts")
    private List<OrderingPartResponse> parts;
}
