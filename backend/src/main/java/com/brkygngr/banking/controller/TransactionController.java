package com.brkygngr.banking.controller;

import com.brkygngr.banking.dto.ExceptionResponse;
import com.brkygngr.banking.dto.transaction.TransactionHistoryResponse;
import com.brkygngr.banking.dto.transaction.TransferMoneyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface TransactionController {

  @Operation(summary = "Transfer money from account to account.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204",
                   description = "Successful response returned when money is transferred."),
      @ApiResponse(responseCode = "400",
                   description = "Failure response that returns request validation errors.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "404",
                   description = "User not found.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "404",
                   description = "Account not found.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "422",
                   description = "Transaction was unsuccessful.")
  })
  public ResponseEntity<?> transferMoney(@ParameterObject TransferMoneyRequest transferMoneyRequest,
                                         JwtAuthenticationToken authentication);

  @Operation(summary = "View account transaction history.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
                   description = "Successful response returned when money is transferred."),
      @ApiResponse(responseCode = "400",
                   description = "Failure response that returns request validation errors.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = TransactionHistoryResponse[].class))),
      @ApiResponse(responseCode = "404",
                   description = "Account not found.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<List<TransactionHistoryResponse>> accountHistory(UUID accountId,
                                                                         JwtAuthenticationToken authentication);
}
