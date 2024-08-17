package com.brkygngr.banking.exception;

public class UserOrPasswordInvalidException extends RuntimeException {

  public UserOrPasswordInvalidException(final String message) {
    super(message);
  }

  public static UserOrPasswordInvalidException withDefaultMessage() {
    return new UserOrPasswordInvalidException("app.identifier.invalid");
  }
}
