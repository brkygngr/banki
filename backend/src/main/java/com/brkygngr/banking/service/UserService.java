package com.brkygngr.banking.service;

import com.brkygngr.banking.dto.LoginUserRequest;
import com.brkygngr.banking.dto.LoginUserResponse;
import com.brkygngr.banking.dto.RegisterUserRequest;
import com.brkygngr.banking.dto.RegisterUserResponse;

public interface UserService {

  RegisterUserResponse registerUser(RegisterUserRequest request);

  LoginUserResponse loginUser(LoginUserRequest request);
}
