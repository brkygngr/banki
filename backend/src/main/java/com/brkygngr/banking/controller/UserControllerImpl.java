package com.brkygngr.banking.controller;

import com.brkygngr.banking.dto.user.LoginUserRequest;
import com.brkygngr.banking.dto.user.LoginUserResponse;
import com.brkygngr.banking.dto.user.RegisterUserRequest;
import com.brkygngr.banking.dto.user.RegisterUserResponse;
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
  @Override
  public ResponseEntity<RegisterUserResponse> registerUser(
      @Valid @RequestBody final RegisterUserRequest registerUserRequest) {
    return ResponseEntity.ok(userService.registerUser(registerUserRequest));
  }

  @PostMapping("/login")
  @Override
  public ResponseEntity<LoginUserResponse> loginUser(
      @Valid @RequestBody final LoginUserRequest loginUserRequest) {
    return ResponseEntity.ok(userService.loginUser(loginUserRequest));
  }
}
