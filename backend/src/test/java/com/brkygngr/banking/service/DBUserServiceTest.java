package com.brkygngr.banking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brkygngr.banking.accessor.KeycloakAccessor;
import com.brkygngr.banking.dto.LoginUserRequest;
import com.brkygngr.banking.dto.LoginUserResponse;
import com.brkygngr.banking.dto.RegisterUserRequest;
import com.brkygngr.banking.dto.RegisterUserResponse;
import com.brkygngr.banking.dto.keycloak.KeycloakTokenResponse;
import com.brkygngr.banking.entity.User;
import com.brkygngr.banking.exception.UserAlreadyExistsException;
import com.brkygngr.banking.exception.UserNotFoundException;
import com.brkygngr.banking.exception.UserOrPasswordInvalidException;
import com.brkygngr.banking.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
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
  private KeycloakAccessor keycloakAccessor;

  @Mock
  private PasswordEncoder passwordEncoder;

  private DBUserService dbUserService;

  @BeforeEach
  void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    dbUserService = new DBUserService(userRepository, keycloakAccessor, passwordEncoder);
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
    void givenUser_whenUserIsSaved_thenReturnsUserId() {
      RegisterUserRequest registerUserRequest = new RegisterUserRequest("username", "decryptedPassword",
                                                                        "existing_email@test.com");

      User saved = new User();
      saved.setId(UUID.randomUUID());

      when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);
      when(passwordEncoder.encode(registerUserRequest.password())).thenReturn("encryptedPassword");
      when(userRepository.save(any(User.class))).thenReturn(saved);

      RegisterUserResponse actual = dbUserService.registerUser(registerUserRequest);

      verify(userRepository).save(any(User.class));
      assertEquals(actual.userId(), saved.getId());
    }

    @Test
    void givenDecryptedPassword_thenEncodesPassword() {
      String encryptedPassword = "encryptedPassword";

      RegisterUserRequest registerUserRequest = new RegisterUserRequest("username", "decryptedPassword",
                                                                        "existing_email@test.com");

      User saved = new User();
      saved.setId(UUID.randomUUID());

      when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);
      when(passwordEncoder.encode(registerUserRequest.password())).thenReturn(encryptedPassword);
      when(userRepository.save(any(User.class))).thenReturn(saved);

      dbUserService.registerUser(registerUserRequest);

      verify(userRepository).save(userArgumentCaptor.capture());

      User result = userArgumentCaptor.getValue();

      assertEquals(encryptedPassword, result.getPassword());
    }
  }

  @Nested
  class LoginUser {

    @Test
    void whenUserDoesNotExists_thenThrowsUserNotFoundException() {
      LoginUserRequest loginUserRequest = new LoginUserRequest("password", "not found user");

      when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());

      assertThrows(UserNotFoundException.class, () -> dbUserService.loginUser(loginUserRequest));
    }

    @Test
    void whenPasswordDoesNotMatch_thenThrowsUserNotFoundException() {
      LoginUserRequest loginUserRequest = new LoginUserRequest("password", "user");

      User existingUser = new User();
      existingUser.setPassword("not matching password");

      when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(existingUser));
      when(passwordEncoder.matches(loginUserRequest.password(), existingUser.getPassword())).thenReturn(false);

      assertThrows(UserOrPasswordInvalidException.class, () -> dbUserService.loginUser(loginUserRequest));
    }

    @Test
    void whenUserExists_thenReturnsAccessToken() {
      User existingUser = new User();
      existingUser.setId(UUID.randomUUID());
      existingUser.setUsername("user");
      existingUser.setPassword("password");

      LoginUserRequest loginUserRequest = new LoginUserRequest("password", "user");

      KeycloakTokenResponse expectedToken = new KeycloakTokenResponse("access_token", 1, "refresh_token", 2);

      when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(existingUser));
      when(passwordEncoder.matches(loginUserRequest.password(), existingUser.getPassword())).thenReturn(true);
      when(keycloakAccessor.loginUser(existingUser)).thenReturn(expectedToken);

      LoginUserResponse result = dbUserService.loginUser(loginUserRequest);

      assertEquals(expectedToken.accessToken(), result.accessToken());
    }
  }
}
