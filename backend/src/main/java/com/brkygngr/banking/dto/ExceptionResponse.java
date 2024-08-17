package com.brkygngr.banking.dto;

import com.brkygngr.banking.exception.ExceptionCode;
import java.time.LocalDateTime;

public record ExceptionResponse(LocalDateTime timestamp, ExceptionCode code, String[] errors) {

}
