package com.brkygngr.banking.service;

import com.brkygngr.banking.dto.user.LoginUserRequest;
import com.brkygngr.banking.dto.user.LoginUserResponse;
import com.brkygngr.banking.dto.user.RegisterUserRequest;
import com.brkygngr.banking.dto.user.RegisterUserResponse;

public interface UserService {

  RegisterUserResponse registerUser(RegisterUserRequest request);

  LoginUserResponse loginUser(LoginUserRequest request);
}
