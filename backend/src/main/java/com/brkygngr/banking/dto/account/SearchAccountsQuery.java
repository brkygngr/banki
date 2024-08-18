package com.brkygngr.banking.dto.account;

import java.util.Optional;

public record SearchAccountsQuery(Optional<String> number, Optional<String> name) {

  public static SearchAccountsQuery empty() {
    return new SearchAccountsQuery(Optional.empty(), Optional.empty());
  }
}
