package com.brkygngr.banking.dto.account;

import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequest(@NotBlank(message = "{app.account.name.required}") String name) {

}
