package com.brkygngr.banking.exception;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {
  INVALID_REQUEST("APP0001"),
  USER_ALREADY_EXISTS("APP0002"),
  USER_NOT_FOUND("APP0003"),
  RESOURCE_ALREADY_EXISTS("APP0004"),
  TRANSACTION_NOT_ENOUGH_MONEY("APP0005");

  private final String code;

  @JsonValue
  @Override
  public String toString() {
    return code;
  }
}
