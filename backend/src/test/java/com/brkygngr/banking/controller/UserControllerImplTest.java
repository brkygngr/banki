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
import com.brkygngr.banking.dto.RegisterUserRequest;
import com.brkygngr.banking.dto.RegisterUserResponse;
import com.brkygngr.banking.exception.ExceptionCode;
import com.brkygngr.banking.exception.UserAlreadyExistsException;
import com.brkygngr.banking.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest({UserControllerImpl.class, SecurityConfig.class, MessageConfig.class})
class UserControllerImplTest {

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
          .andExpect(jsonPath("$.errors").value("Username is required!"));
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
          .andExpect(jsonPath("$.errors").value("Username must be shorter than 255 characters!"));
    }

    @Test
    void whenRequestDoesNotHavePassword_thenReturnsBadRequest() throws Exception {
      String[] errors = Stream.of(
              "Password must be longer than 8 and shorter than 255 characters!",
              "Password is required!",
              "Password must contain at least one digit, "
                  + "one lowercase letter, one uppercase letter, and one special character!")
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
      String[] errors = {"Password must be longer than 8 and shorter than 255 characters!"};

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
      String[] errors = {"Password must be longer than 8 and shorter than 255 characters!"};

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
      String[] errors = {
          "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special "
              + "character!"
      };

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
          "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special "
              + "character!"
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
          "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special "
              + "character!"
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
          "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special "
              + "character!"
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
          "Email is required!"
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
          "Email must be a valid email address."
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
          "User already exists!"
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
          .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.getCode()))
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
}
