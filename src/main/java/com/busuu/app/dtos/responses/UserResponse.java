package com.busuu.app.dtos.responses;

import com.busuu.app.entities.Role;
import com.busuu.app.entities.enums.PresenceStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.Instant;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse extends BaseResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("avatar_name")
    private String avatarName;

    private String country;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("last_seen_at")
    private Instant lastSeenAt;

    private PresenceStatus status;

    @JsonProperty("roles")
    private List<Role> roles;
}
