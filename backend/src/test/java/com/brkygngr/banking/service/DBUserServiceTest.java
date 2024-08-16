package com.brkygngr.banking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brkygngr.banking.dto.RegisterUserRequest;
import com.brkygngr.banking.entity.User;
import com.brkygngr.banking.exception.UserAlreadyExistsException;
import com.brkygngr.banking.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class DBUserServiceTest {

  @Captor
  private ArgumentCaptor<User> userArgumentCaptor;

  private AutoCloseable autoCloseable;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  private DBUserService dbUserService;

  @BeforeEach
  void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    dbUserService = new DBUserService(userRepository, passwordEncoder);
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }

  @Nested
  class RegisterUser {

    @Test
    void whenUsernameAlreadyExists_thenThrowsUserAlreadyExistsException() {
      RegisterUserRequest registerUserRequest = new RegisterUserRequest("existing_username", "password",
          "test@test.com");

      when(userRepository.existsByUsernameOrEmail(eq(registerUserRequest.username()), anyString())).thenReturn(true);

      assertThrows(UserAlreadyExistsException.class, () -> dbUserService.registerUser(registerUserRequest));
    }

    @Test
    void whenEmailAlreadyExists_thenThrowsUserAlreadyExistsException() {
      RegisterUserRequest registerUserRequest = new RegisterUserRequest("username", "password",
          "existing_email@test.com");

      when(userRepository.existsByUsernameOrEmail(anyString(), eq(registerUserRequest.email()))).thenReturn(true);

      assertThrows(UserAlreadyExistsException.class, () -> dbUserService.registerUser(registerUserRequest));
    }

    @Test
    void givenUser_thenSavesUserToDB() {
      RegisterUserRequest registerUserRequest = new RegisterUserRequest("username", "decryptedPassword",
          "existing_email@test.com");

      when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);
      when(passwordEncoder.encode(registerUserRequest.password())).thenReturn("encryptedPassword");

      dbUserService.registerUser(registerUserRequest);

      verify(userRepository).save(any(User.class));
    }

    @Test
    void givenDecryptedPassword_thenEncodesPassword() {
      String encryptedPassword = "encryptedPassword";

      RegisterUserRequest registerUserRequest = new RegisterUserRequest("username", "decryptedPassword",
          "existing_email@test.com");

      when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);
      when(passwordEncoder.encode(registerUserRequest.password())).thenReturn(encryptedPassword);

      dbUserService.registerUser(registerUserRequest);

      verify(userRepository).save(userArgumentCaptor.capture());

      User result = userArgumentCaptor.getValue();

      assertEquals(encryptedPassword, result.getPassword());
    }
  }
}
