package com.brkygngr.banking.service;

import com.brkygngr.banking.accessor.KeycloakAccessor;
import com.brkygngr.banking.dto.KeycloakTokenResponse;
import com.brkygngr.banking.dto.LoginUserRequest;
import com.brkygngr.banking.dto.LoginUserResponse;
import com.brkygngr.banking.dto.RegisterUserRequest;
import com.brkygngr.banking.dto.RegisterUserResponse;
import com.brkygngr.banking.entity.User;
import com.brkygngr.banking.exception.UserAlreadyExistsException;
import com.brkygngr.banking.exception.UserNotFoundException;
import com.brkygngr.banking.exception.UserOrPasswordInvalidException;
import com.brkygngr.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DBUserService implements UserService {

  private final UserRepository userRepository;

  private final KeycloakAccessor keycloakAccessor;

  private final PasswordEncoder passwordEncoder;

  @Override
  public RegisterUserResponse registerUser(final RegisterUserRequest request) {
    boolean isUserExists = userRepository.existsByUsernameOrEmail(request.username(), request.email());

    if (isUserExists) {
      throw UserAlreadyExistsException.withDefaultMessage();
    }

    User user = new User();
    user.setEmail(request.email());
    user.setUsername(request.username());
    user.setPassword(passwordEncoder.encode(request.password()));

    String result = keycloakAccessor.registerUser(user);

    User saved = userRepository.save(user);

    return new RegisterUserResponse(saved.getId());
  }

  @Override
  public LoginUserResponse loginUser(final LoginUserRequest request) {
    User user = userRepository.findByUsernameOrEmail(request.identifier(), request.identifier())
        .orElseThrow(UserNotFoundException::withDefaultMessage);

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw UserOrPasswordInvalidException.withDefaultMessage();
    }

    KeycloakTokenResponse keycloakTokenResponse = keycloakAccessor.loginUser(user);

    return new LoginUserResponse(keycloakTokenResponse.accessToken());
  }
}
