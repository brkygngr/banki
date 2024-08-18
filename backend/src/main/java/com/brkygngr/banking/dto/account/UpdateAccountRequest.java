package com.brkygngr.banking.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateAccountRequest(@NotBlank(message = "{app.account.name.required}")
                                   String name,
                                   @NotNull(message = "{app.account.balance.required}")
                                   @Positive(message = "{app.account.balance.positive}")
                                   BigDecimal balance) {

}
