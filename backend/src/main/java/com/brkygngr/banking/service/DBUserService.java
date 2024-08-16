package com.brkygngr.banking.service;

import com.brkygngr.banking.dto.RegisterUserRequest;
import com.brkygngr.banking.dto.RegisterUserResponse;
import com.brkygngr.banking.entity.User;
import com.brkygngr.banking.exception.UserAlreadyExistsException;
import com.brkygngr.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DBUserService implements UserService {

  private final UserRepository userRepository;

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

    User saved = userRepository.save(user);

    return new RegisterUserResponse(saved.getId());
  }
}
