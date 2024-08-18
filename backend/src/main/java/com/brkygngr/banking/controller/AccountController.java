package com.brkygngr.banking.controller;

import com.brkygngr.banking.dto.ExceptionResponse;
import com.brkygngr.banking.dto.account.AccountResponse;
import com.brkygngr.banking.dto.account.CreateAccountRequest;
import com.brkygngr.banking.dto.account.CreateAccountResponse;
import com.brkygngr.banking.dto.account.SearchAccountsQuery;
import com.brkygngr.banking.dto.account.UpdateAccountRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface AccountController {

  @Operation(summary = "Creates an account for the authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201",
                   description = "Successful response that returns created account's id.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = CreateAccountResponse.class))),
      @ApiResponse(responseCode = "400",
                   description = "Failure response that returns request validation errors.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "404",
                   description = "Failure response when user is not found.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<CreateAccountResponse> createAccount(@ParameterObject CreateAccountRequest createAccountRequest,
                                                             JwtAuthenticationToken authentication);

  @Operation(summary = "Searches accounts.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
                   description = "Successful response that returns found accounts.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Page.class))),
      @ApiResponse(responseCode = "400",
                   description = "Failure response that returns request validation errors",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Page<AccountResponse>> searchAccounts(@ParameterObject SearchAccountsQuery searchAccountsQuery,
                                                              @ParameterObject Pageable pageable,
                                                              JwtAuthenticationToken authentication);

  @Operation(summary = "Updates account.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204",
                   description = "Successful response returned when account is updated."),
      @ApiResponse(responseCode = "400",
                   description = "Failure response that returns request validation errors.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "404",
                   description = "Account not found.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Void> updateAccount(UUID accountId,
                                            @ParameterObject UpdateAccountRequest updateAccountRequest,
                                            JwtAuthenticationToken authentication);

  @Operation(summary = "Deletes account.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204",
                   description = "Successful response returned when account is deleted."),
      @ApiResponse(responseCode = "400",
                   description = "Failure response that returns request validation errors",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "404",
                   description = "Account not found.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Void> deleteAccount(UUID accountId, JwtAuthenticationToken authentication);

  @Operation(summary = "View account details.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
                   description = "Successful response returned when user is authenticated.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = AccountResponse.class))),
      @ApiResponse(responseCode = "400",
                   description = "Failure response that returns request validation errors",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "404",
                   description = "Account not found.",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<AccountResponse> getAccount(UUID accountId, JwtAuthenticationToken authentication);

}
