package com.busuu.app.dtos.responses.question;

import com.busuu.app.dtos.responses.BaseResponse;
import com.busuu.app.entities.questions.QuestionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuestionResponse extends BaseResponse {

    @JsonProperty("question_id")
    private String id;

    @JsonProperty("request")
    private String request;

    @JsonProperty("question_text")
    private String questionText;

    @JsonProperty("question_type")
    private QuestionType questionType;

    @JsonProperty("mark")
    private Integer mark;

    @JsonProperty("explanation")
    private String explanation;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("image_name")
    private String imageName;

    @JsonProperty("video_url")
    private String videoUrl;

    @JsonProperty("video_name")
    private String videoName;

    @JsonProperty("audio_url")
    private String audioUrl;

    @JsonProperty("audio_name")
    private String audioName;

    @JsonProperty("question_order")
    private Integer questionOrder;

    @JsonProperty("lesson_id")
    private String lessonId;

    @JsonProperty("grammar_section_id")
    private String grammarSectionId;
}
