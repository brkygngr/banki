package com.brkygngr.banking.dto;

import com.brkygngr.banking.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
    @NotBlank(message = "{APP_USERNAME_REQUIRED}")
    @Size(max = 255, message = "{APP_USERNAME_SIZE_INVALID}")
    String username,
    @Password
    String password,
    @Email(message = "{APP_EMAIL_INVALID}")
    String email
) {

}
