package com.brkygngr.banking.dto.user;

import com.brkygngr.banking.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
    @NotBlank(message = "{app.username.required}")
    @Size(max = 255, message = "{app.username.size.invalid}")
    String username,
    @Password
    String password,
    @NotBlank(message = "{app.email.required}")
    @Email(message = "{app.email.invalid}")
    String email
) {

}
