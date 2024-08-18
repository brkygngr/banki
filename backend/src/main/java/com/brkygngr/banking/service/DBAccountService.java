package com.brkygngr.banking.service;

import com.brkygngr.banking.dto.account.AccountResponse;
import com.brkygngr.banking.dto.account.CreateAccountRequest;
import com.brkygngr.banking.dto.account.CreateAccountResponse;
import com.brkygngr.banking.dto.account.SearchAccountsQuery;
import com.brkygngr.banking.dto.account.UpdateAccountRequest;
import com.brkygngr.banking.entity.Account;
import com.brkygngr.banking.entity.User;
import com.brkygngr.banking.exception.AccountNotFoundException;
import com.brkygngr.banking.exception.UserNotFoundException;
import com.brkygngr.banking.repository.AccountRepository;
import com.brkygngr.banking.repository.UserRepository;
import com.brkygngr.banking.repository.specification.AccountSearchCriteria;
import com.brkygngr.banking.repository.specification.AccountSpecification;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DBAccountService implements AccountService {

  private static final SecureRandom random = new SecureRandom();

  private static final int ACCOUNT_NUMBER_LENGTH = 16;

  private final UserRepository userRepository;

  private final AccountRepository accountRepository;

  @Override
  public CreateAccountResponse createAccount(final String username,
                                             final CreateAccountRequest createAccountRequest) {
    User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::withDefaultMessage);

    log.info("User#{} creating account '{}'", user.getId(), createAccountRequest.name());

    StringBuilder accountNumber = new StringBuilder(ACCOUNT_NUMBER_LENGTH);

    for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
      accountNumber.append(random.nextInt(10));
    }

    Account account = new Account();
    account.setNumber(accountNumber.toString());
    account.setName(createAccountRequest.name());
    account.setUser(user);
    account.setBalance(BigDecimal.ZERO);

    Account saved = accountRepository.save(account);

    log.info("User#{} created account#{} '{}'", user.getId(), account.getId(), account.getName());

    return new CreateAccountResponse(saved.getId());
  }

  @Override
  public Page<AccountResponse> searchAccounts(final String username,
                                              final SearchAccountsQuery searchAccountsQuery,
                                              final Pageable pageable) {
    User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::withDefaultMessage);

    log.info("User#{} searching accounts with number '{}' and name '{}'",
             user.getId(),
             searchAccountsQuery.number(),
             searchAccountsQuery.name());

    String number = searchAccountsQuery.number().orElse("");
    String name = searchAccountsQuery.name().orElse("");

    AccountSearchCriteria accountSearchCriteria = new AccountSearchCriteria(user, number, name);

    Page<Account> accountPage = accountRepository.findAll(new AccountSpecification(accountSearchCriteria), pageable);

    log.info("User#{} found total of {} accounts with number '{}' and name '{}'",
             user.getId(),
             accountPage.getTotalElements(),
             number,
             name);

    return accountPage.map(AccountResponse::fromAccount);
  }

  @Override
  public void updateAccount(String username, UUID accountId, UpdateAccountRequest updateAccountRequest) {
    User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::withDefaultMessage);

    log.info("User#{} updating account#{}", user.getId(), accountId);

    Account account = accountRepository.findByIdAndUser(accountId, user)
                                       .orElseThrow(AccountNotFoundException::withDefaultMessage);

    account.setName(updateAccountRequest.name());
    account.setBalance(updateAccountRequest.balance());

    accountRepository.save(account);

    log.info("User#{} updated account#{}", user.getId(), account.getId());
  }

  @Transactional
  @Override
  public void deleteAccount(String username, UUID accountId) {
    User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::withDefaultMessage);

    if (!accountRepository.existsByIdAndUser(accountId, user)) {
      throw AccountNotFoundException.withDefaultMessage();
    }

    log.info("User#{} deleting account#{}", user.getId(), accountId);

    accountRepository.deleteByIdAndUser(accountId, user);

    log.info("User#{} deleted account#{}", user.getId(), accountId);
  }

  @Override
  public AccountResponse getAccount(String username, UUID accountId) {
    User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::withDefaultMessage);

    log.info("User#{} getting account#{}", user.getId(), accountId);

    Account account = accountRepository.findByIdAndUser(accountId, user)
                                       .orElseThrow(AccountNotFoundException::withDefaultMessage);

    AccountResponse response = AccountResponse.fromAccount(account);

    log.info("User#{} fetched account#{}", user.getId(), response.id());

    return response;
  }
}
