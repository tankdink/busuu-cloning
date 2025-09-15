package com.busuu.app.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LanguageResponse extends BaseResponse
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("flag_icon_url")
    private String flagIconUrl;

    @JsonProperty("flag_icon_name")
    private String flagIconName;

    @JsonProperty("total_users_learning")
    private Long totalUsersLearning;
}
