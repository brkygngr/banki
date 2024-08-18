package com.brkygngr.banking.service;

import com.brkygngr.banking.dto.account.AccountResponse;
import com.brkygngr.banking.dto.account.CreateAccountRequest;
import com.brkygngr.banking.dto.account.CreateAccountResponse;
import com.brkygngr.banking.dto.account.SearchAccountsQuery;
import com.brkygngr.banking.dto.account.UpdateAccountRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

  CreateAccountResponse createAccount(String username, CreateAccountRequest createAccountRequest);

  Page<AccountResponse> searchAccounts(String username, SearchAccountsQuery searchAccountsQuery, Pageable pageable);


  void updateAccount(String username, UUID accountId, UpdateAccountRequest updateAccountRequest);


  void deleteAccount(String username, UUID accountId);


  AccountResponse getAccount(String username, UUID accountId);
}
