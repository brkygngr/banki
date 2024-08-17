package com.brkygngr.banking.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.brkygngr.banking.configuration.MessageConfig;
import com.brkygngr.banking.configuration.SecurityConfig;
import com.brkygngr.banking.dto.ExceptionResponse;
import com.brkygngr.banking.dto.user.LoginUserRequest;
import com.brkygngr.banking.dto.user.LoginUserResponse;
import com.brkygngr.banking.dto.user.RegisterUserRequest;
import com.brkygngr.banking.dto.user.RegisterUserResponse;
import com.brkygngr.banking.exception.ExceptionCode;
import com.brkygngr.banking.exception.UserAlreadyExistsException;
import com.brkygngr.banking.exception.UserNotFoundException;
import com.brkygngr.banking.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest({UserControllerImpl.class, SecurityConfig.class, MessageConfig.class})
class UserControllerImplTest {

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Nested
  class RegisterUser {

    @Test
    void whenRequestDoesNotHaveUsername_thenReturnsBadRequest() throws Exception {
      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "",
          "Pa55word.",
          "test@email.com"
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(
          new RegisterUserResponse(UUID.randomUUID()));

      mockMvc.perform(post("/api/users/register")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(registerUserRequest)))
             .andExpect(status().isBadRequest())
             .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
             .andExpect(jsonPath("$.errors").value(messageSource.getMessage("app.username.required",
                                                                            null,
                                                                            Locale.ENGLISH)));
    }

    @Test
    void whenRequestUsernameLongerThan255_thenReturnsBadRequest() throws Exception {
      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "user123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123"
              + "45678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234"
              + "5678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234"
              + "5678901234567890123456789012345678901234567890",
          "Pa55word.",
          "test@email.com"
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(
          new RegisterUserResponse(UUID.randomUUID()));

      mockMvc.perform(post("/api/users/register")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(registerUserRequest)))
             .andExpect(status().isBadRequest())
             .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
             .andExpect(jsonPath("$.errors").value(messageSource.getMessage("app.username.size.invalid",
                                                                            null,
                                                                            Locale.ENGLISH)));
    }

    @Test
    void whenRequestDoesNotHavePassword_thenReturnsBadRequest() throws Exception {
      String[] errors = Stream.of(messageSource.getMessage("app.password.size.invalid",
                                                           null,
                                                           Locale.ENGLISH),
                                  messageSource.getMessage("app.password.required",
                                                           null,
                                                           Locale.ENGLISH),
                                  messageSource.getMessage("app.password.pattern.invalid",
                                                           null,
                                                           Locale.ENGLISH))
                              .sorted()
                              .toArray(String[]::new);

      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "username",
          "",
          "test@email.com"
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(
          new RegisterUserResponse(UUID.randomUUID()));

      MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(registerUserRequest)))
                                   .andExpect(status().isBadRequest())
                                   .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
                                   .andReturn();

      ExceptionResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                        ExceptionResponse.class);

      assertArrayEquals(errors, Arrays.stream(result.errors()).sorted().toArray(String[]::new));
    }

    @Test
    void whenRequestPasswordIsShorterThan8_thenReturnsBadRequest() throws Exception {
      String[] errors = {messageSource.getMessage("app.password.size.invalid",
                                                  null,
                                                  Locale.ENGLISH)};

      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "username",
          "aA1.",
          "test@email.com"
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(
          new RegisterUserResponse(UUID.randomUUID()));

      MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(registerUserRequest)))
                                   .andExpect(status().isBadRequest())
                                   .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
                                   .andReturn();

      ExceptionResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                        ExceptionResponse.class);

      assertArrayEquals(errors, Arrays.stream(result.errors()).sorted().toArray(String[]::new));
    }

    @Test
    void whenRequestPasswordIsLongerThan255_thenReturnsBadRequest() throws Exception {
      String[] errors = {messageSource.getMessage("app.password.size.invalid",
                                                  null,
                                                  Locale.ENGLISH)};

      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "username",
          "Pass.123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123"
              + "45678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234"
              + "5678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234"
              + "5678901234567890123456789012345678901234567890",
          "test@email.com"
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(
          new RegisterUserResponse(UUID.randomUUID()));

      MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(registerUserRequest)))
                                   .andExpect(status().isBadRequest())
                                   .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
                                   .andReturn();

      ExceptionResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                        ExceptionResponse.class);

      assertArrayEquals(errors, Arrays.stream(result.errors()).sorted().toArray(String[]::new));
    }

    @Test
    void whenRequestPasswordDoesNotHaveAtLeastOneDigit_thenReturnsBadRequest() throws Exception {
      String[] errors = {messageSource.getMessage("app.password.pattern.invalid",
                                                  null,
                                                  Locale.ENGLISH)};

      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "username",
          "userPassword.",
          "test@email.com"
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(
          new RegisterUserResponse(UUID.randomUUID()));

      MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(registerUserRequest)))
                                   .andExpect(status().isBadRequest())
                                   .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
                                   .andReturn();

      ExceptionResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                        ExceptionResponse.class);

      assertArrayEquals(errors, Arrays.stream(result.errors()).sorted().toArray(String[]::new));
    }

    @Test
    void whenRequestPasswordDoesNotHaveAtLeastOneLowercaseChar_thenReturnsBadRequest() throws Exception {
      String[] errors = {
          messageSource.getMessage("app.password.pattern.invalid",
                                   null,
                                   Locale.ENGLISH)
      };

      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "username",
          "123456789A.",
          "test@email.com"
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(
          new RegisterUserResponse(UUID.randomUUID()));

      MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(registerUserRequest)))
                                   .andExpect(status().isBadRequest())
                                   .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
                                   .andReturn();

      ExceptionResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                        ExceptionResponse.class);

      assertArrayEquals(errors, Arrays.stream(result.errors()).sorted().toArray(String[]::new));
    }

    @Test
    void whenRequestPasswordDoesNotHaveAtLeastOneUpperChar_thenReturnsBadRequest() throws Exception {
      String[] errors = {
          messageSource.getMessage("app.password.pattern.invalid",
                                   null,
                                   Locale.ENGLISH)
      };

      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "username",
          "123456789a.",
          "test@email.com"
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(
          new RegisterUserResponse(UUID.randomUUID()));

      MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(registerUserRequest)))
                                   .andExpect(status().isBadRequest())
                                   .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
                                   .andReturn();

      ExceptionResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                        ExceptionResponse.class);

      assertArrayEquals(errors, Arrays.stream(result.errors()).sorted().toArray(String[]::new));
    }

    @Test
    void whenRequestPasswordDoesNotHaveAtLeastOneSpecialChar_thenReturnsBadRequest() throws Exception {
      String[] errors = {
          messageSource.getMessage("app.password.pattern.invalid",
                                   null,
                                   Locale.ENGLISH)
      };

      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "username",
          "aA12345678",
          "test@email.com"
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(
          new RegisterUserResponse(UUID.randomUUID()));

      MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(registerUserRequest)))
                                   .andExpect(status().isBadRequest())
                                   .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
                                   .andReturn();

      ExceptionResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                        ExceptionResponse.class);

      assertArrayEquals(errors, Arrays.stream(result.errors()).sorted().toArray(String[]::new));
    }

    @Test
    void whenRequestEmailIsNull_thenReturnsBadRequest() throws Exception {
      String[] errors = {
          messageSource.getMessage("app.email.required",
                                   null,
                                   Locale.ENGLISH)
      };

      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "username",
          "aA12345678.",
          ""
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(
          new RegisterUserResponse(UUID.randomUUID()));

      MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(registerUserRequest)))
                                   .andExpect(status().isBadRequest())
                                   .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
                                   .andReturn();

      ExceptionResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                        ExceptionResponse.class);

      assertArrayEquals(errors, Arrays.stream(result.errors()).sorted().toArray(String[]::new));
    }

    @Test
    void whenRequestEmailUsesInvalidFormat_thenReturnsBadRequest() throws Exception {
      String[] errors = {
          messageSource.getMessage("app.email.invalid",
                                   null,
                                   Locale.ENGLISH)
      };

      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "username",
          "aA12345678.",
          "invalid format"
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(
          new RegisterUserResponse(UUID.randomUUID()));

      MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(registerUserRequest)))
                                   .andExpect(status().isBadRequest())
                                   .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
                                   .andReturn();

      ExceptionResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                        ExceptionResponse.class);

      assertArrayEquals(errors, Arrays.stream(result.errors()).sorted().toArray(String[]::new));
    }

    @Test
    void whenUserAlreadyRegistered_thenReturnsBadRequest() throws Exception {
      String[] errors = {
          messageSource.getMessage("app.user.already.exists",
                                   null,
                                   Locale.ENGLISH)
      };

      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "username",
          "aA12345678.",
          "test@email.com"
      );

      when(userService.registerUser(any(RegisterUserRequest.class))).thenThrow(
          UserAlreadyExistsException.withDefaultMessage());

      MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(registerUserRequest)))
                                   .andExpect(status().isBadRequest())
                                   .andExpect(jsonPath("$.code").value(ExceptionCode.USER_ALREADY_EXISTS.getCode()))
                                   .andReturn();

      ExceptionResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                        ExceptionResponse.class);

      assertArrayEquals(errors, Arrays.stream(result.errors()).sorted().toArray(String[]::new));
    }

    @Test
    void whenUserRegistered_thenReturnsUserId() throws Exception {
      RegisterUserRequest registerUserRequest = new RegisterUserRequest(
          "username",
          "aA12345678.",
          "test@email.com"
      );

      RegisterUserResponse registerUserResponse = new RegisterUserResponse(UUID.randomUUID());

      when(userService.registerUser(any(RegisterUserRequest.class))).thenReturn(registerUserResponse);

      mockMvc.perform(post("/api/users/register")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(registerUserRequest)))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.userId").value(registerUserResponse.userId().toString()));
    }
  }

  @Nested
  class LoginUser {

    @Test
    void whenRequestDoesNotHaveIdentifier_thenReturnsBadRequest() throws Exception {
      LoginUserRequest loginUserRequest = new LoginUserRequest(
          "Pa55word.",
          ""
      );

      when(userService.loginUser(any(LoginUserRequest.class))).thenReturn(
          new LoginUserResponse(""));

      mockMvc.perform(post("/api/users/login")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(loginUserRequest)))
             .andExpect(status().isBadRequest())
             .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
             .andExpect(jsonPath("$.errors").value(messageSource.getMessage("app.identifier.required",
                                                                            null,
                                                                            Locale.ENGLISH)));
    }

    @Test
    void whenRequestDoesNotHavePassword_thenReturnsBadRequest() throws Exception {
      LoginUserRequest loginUserRequest = new LoginUserRequest(
          "",
          "identifier"
      );

      String[] errors = Stream.of(messageSource.getMessage("app.password.size.invalid",
                                                           null,
                                                           Locale.ENGLISH),
                                  messageSource.getMessage("app.password.required",
                                                           null,
                                                           Locale.ENGLISH),
                                  messageSource.getMessage("app.password.pattern.invalid",
                                                           null,
                                                           Locale.ENGLISH))
                              .sorted()
                              .toArray(String[]::new);

      when(userService.loginUser(any(LoginUserRequest.class))).thenReturn(
          new LoginUserResponse(""));

      MvcResult mvcResult = mockMvc.perform(post("/api/users/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(loginUserRequest)))
                                   .andExpect(status().isBadRequest())
                                   .andReturn();

      ExceptionResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                        ExceptionResponse.class);

      assertArrayEquals(errors, Arrays.stream(result.errors()).sorted().toArray(String[]::new));
    }

    @Test
    void whenUserDoesNotExists_thenReturnsNotFound() throws Exception {
      LoginUserRequest loginUserRequest = new LoginUserRequest(
          "aA12345678.",
          "identifier"
      );

      when(userService.loginUser(any(LoginUserRequest.class))).thenThrow(UserNotFoundException.withDefaultMessage());

      mockMvc.perform(post("/api/users/login")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(loginUserRequest)))
             .andExpect(status().isNotFound())
             .andExpect(jsonPath("$.code").value(ExceptionCode.USER_NOT_FOUND.getCode()))
             .andExpect(jsonPath("$.errors").value(messageSource.getMessage("app.user.not.found",
                                                                            null,
                                                                            Locale.ENGLISH)));
    }

    @Test
    void whenUserExists_thenReturnsToken() throws Exception {
      LoginUserRequest loginUserRequest = new LoginUserRequest(
          "aA12345678.",
          "identifier"
      );

      when(userService.loginUser(any(LoginUserRequest.class))).thenReturn(new LoginUserResponse("access_token"));

      mockMvc.perform(post("/api/users/login")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(loginUserRequest)))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.accessToken").value("access_token"));
    }
  }
}
