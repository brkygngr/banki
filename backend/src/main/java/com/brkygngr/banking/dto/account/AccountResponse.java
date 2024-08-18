package com.brkygngr.banking.dto.account;

import com.brkygngr.banking.entity.Account;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(UUID id, String number, String name, BigDecimal balance) {

  public static AccountResponse fromAccount(Account account) {
    return new AccountResponse(account.getId(), account.getNumber(), account.getName(), account.getBalance());
  }
}
