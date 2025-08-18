package com.busuu.app.dtos.requests.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
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
public class UserLoginDTO extends SocialAccountDTO {
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    // Facebook Account Id, not mandatory, can be blank
    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    // Google Account Id, not mandatory, can be blank
    @JsonProperty("google_account_id")
    private String googleAccountId;

    //For Google, Facebook login
    // Full name, not mandatory, can be blank
    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    // Profile image URL, not mandatory, can be blank
    @JsonProperty("avatar")
    private String avatar;

    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }
    public boolean isFacebookAccountIdValid() {
        return facebookAccountId != null && !facebookAccountId.isEmpty();
    }
    public boolean isGoogleAccountIdValid() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }
}
