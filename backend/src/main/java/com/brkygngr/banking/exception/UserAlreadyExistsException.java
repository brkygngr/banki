package com.brkygngr.banking.exception;

public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(final String message) {
    super(message);
  }

  public static UserAlreadyExistsException withDefaultMessage() {
    return new UserAlreadyExistsException("app.user.already.exists");
  }
}
