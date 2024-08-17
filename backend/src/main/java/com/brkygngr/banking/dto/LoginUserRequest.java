package com.brkygngr.banking.dto;

import com.brkygngr.banking.validation.Password;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(
    @Password
    String password,
    @NotBlank(message = "{app.identifier.required}")
    String identifier
) {

}
