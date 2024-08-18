package com.brkygngr.banking.exception;

public class AccountNotFoundException extends RuntimeException {

  public AccountNotFoundException(final String message) {
    super(message);
  }

  public static AccountNotFoundException withDefaultMessage() {
    return new AccountNotFoundException("app.account.not.found");
  }
}
