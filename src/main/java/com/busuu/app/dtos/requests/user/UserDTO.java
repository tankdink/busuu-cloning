package com.busuu.app.dtos.requests.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("full_name")
    private String fullName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^0\\d{9}$",
            message = "Phone number must be valid"
    )
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Retype password is required")
    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("role_ids")
    @NotNull(message = "Role IDs are required")
    @Size(min = 1, message = "There must be at least one role ID")
    private List<String> roleIds;
}
