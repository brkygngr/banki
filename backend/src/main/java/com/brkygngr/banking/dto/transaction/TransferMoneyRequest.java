package com.brkygngr.banking.dto.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferMoneyRequest(@NotNull(message = "{app.transaction.from.required}") UUID from,
                                   @NotNull(message = "{app.transaction.to.required}") UUID to,
                                   @NotNull(message = "{app.transaction.amount.required}")
                                   @Positive(message = "{app.transaction.amount.positive}") BigDecimal amount) {

}
