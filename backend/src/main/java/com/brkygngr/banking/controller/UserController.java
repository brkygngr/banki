package com.brkygngr.banking.controller;

import com.brkygngr.banking.dto.ExceptionResponse;
import com.brkygngr.banking.dto.LoginUserRequest;
import com.brkygngr.banking.dto.LoginUserResponse;
import com.brkygngr.banking.dto.RegisterUserRequest;
import com.brkygngr.banking.dto.RegisterUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface UserController {

  @Operation(summary = "Registers given user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
                   description = "Successful response that returns registered user's id",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = RegisterUserResponse.class))),
      @ApiResponse(responseCode = "400",
                   description = "Failure response that returns request validation errors",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<RegisterUserResponse> registerUser(RegisterUserRequest registerUserRequest);

  @Operation(summary = "Logins given user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
                   description = "Successful response that returns logged in user's access token",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = LoginUserResponse.class))),
      @ApiResponse(responseCode = "400",
                   description = "Failure response that returns request validation errors",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<LoginUserResponse> loginUser(LoginUserRequest loginUserRequest);
}
