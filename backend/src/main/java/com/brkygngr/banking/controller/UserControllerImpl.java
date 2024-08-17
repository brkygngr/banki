package com.brkygngr.banking.controller;

import com.brkygngr.banking.dto.RegisterUserRequest;
import com.brkygngr.banking.dto.RegisterUserResponse;
import com.brkygngr.banking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserControllerImpl implements UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<RegisterUserResponse> registerUser(
      @Valid @RequestBody final RegisterUserRequest registerUserRequest) {
    return ResponseEntity.ok(userService.registerUser(registerUserRequest));
  }
}
