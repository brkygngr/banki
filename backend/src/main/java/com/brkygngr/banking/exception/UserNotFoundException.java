package com.brkygngr.banking.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(final String message) {
    super(message);
  }

  public static UserNotFoundException withDefaultMessage() {
    return new UserNotFoundException("app.user.not.found");
  }
}
