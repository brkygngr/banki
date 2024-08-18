package com.brkygngr.banking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.brkygngr.banking.repository.specification.AccountSpecification;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class DBAccountServiceTest {

  private AutoCloseable autoCloseable;

  @Captor
  private ArgumentCaptor<Account> accountArgumentCaptor;

  @Mock
  private UserRepository userRepository;

  @Mock
  private AccountRepository accountRepository;

  private DBAccountService dbAccountService;

  @BeforeEach
  void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    dbAccountService = new DBAccountService(userRepository, accountRepository);
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }

  @Test
  void createAccount_whenUserNotFound_thenThrowException() {
    String username = "nonExistentUser";

    CreateAccountRequest request = new CreateAccountRequest("Test Account");

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> dbAccountService.createAccount(username, request));
  }

  @Test
  void createAccount_whenAccountIsCreated_thenRandomNumberIsAssigned() {
    User user = new User();
    user.setUsername("test user");
    user.setId(UUID.randomUUID());

    Account account = new Account();
    account.setNumber("123");
    account.setName("test acc");
    account.setUser(user);
    account.setBalance(BigDecimal.ZERO);

    when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    when(accountRepository.save(accountArgumentCaptor.capture())).thenReturn(account);

    CreateAccountResponse response = dbAccountService.createAccount(user.getUsername(),
                                                                    new CreateAccountRequest("Test Account"));

    assertEquals(account.getId(), response.accountId());
    assertNotNull(accountArgumentCaptor.getValue().getNumber());
  }

  @Test
  void searchAccounts_whenUserNotFound_thenThrowException() {
    String username = "nonExistentUser";

    SearchAccountsQuery searchAccountsQuery = new SearchAccountsQuery(Optional.of("number"), Optional.of("name"));

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
                 () -> dbAccountService.searchAccounts(username, searchAccountsQuery, Pageable.unpaged()));
  }

  @Test
  void searchAccounts_whenQueried_thenUsesAccountSpecification() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("test user");

    SearchAccountsQuery searchAccountsQuery = new SearchAccountsQuery(Optional.of("number"), Optional.of("name"));

    when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    when(accountRepository.findAll(any(AccountSpecification.class), any(Pageable.class))).thenReturn(Page.empty());

    dbAccountService.searchAccounts(user.getUsername(),
                                    searchAccountsQuery,
                                    Pageable.unpaged());

    verify(accountRepository).findAll(any(AccountSpecification.class), any(Pageable.class));
  }

  @Test
  void updateAccount_whenAccountDoesNotExists_thenThrowsException() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("test user");

    UUID accountId = UUID.randomUUID();

    when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class,
                 () -> dbAccountService.updateAccount(user.getUsername(),
                                                      accountId,
                                                      new UpdateAccountRequest("Updated Account Name")));
  }

  @Test
  void updateAccount_whenAccountExists_thenUpdatesAccountName() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("test user");

    Account account = new Account();
    account.setId(UUID.randomUUID());
    account.setName("Old Account Name");

    UpdateAccountRequest request = new UpdateAccountRequest("Updated Account Name");

    when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    when(accountRepository.findByIdAndUser(account.getId(), user)).thenReturn(Optional.of(account));

    dbAccountService.updateAccount(user.getUsername(), account.getId(), request);

    assertEquals(request.name(), account.getName());
  }

  @Test
  void deleteAccount_whenUserNotFound_thenThrowsException() {
    String username = "nonExistentUser";

    UUID accountId = UUID.randomUUID();

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> dbAccountService.deleteAccount(username, accountId));
  }

  @Test
  void deleteAccount_whenAccountNotFound_thenThrowsException() {
    String username = "testUser";

    User user = new User();
    user.setUsername(username);
    user.setId(UUID.randomUUID());

    UUID accountId = UUID.randomUUID();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(accountRepository.existsByIdAndUser(accountId, user)).thenReturn(false);

    assertThrows(AccountNotFoundException.class, () -> dbAccountService.deleteAccount(username, accountId));
  }

  @Test
  void deleteAccount_whenAccountExists_thenDeletesAccount() {
    String username = "testUser";

    User user = new User();
    user.setUsername(username);
    user.setId(UUID.randomUUID());

    UUID accountId = UUID.randomUUID();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(accountRepository.existsByIdAndUser(accountId, user)).thenReturn(true);

    dbAccountService.deleteAccount(username, accountId);

    verify(accountRepository).deleteByIdAndUser(accountId, user);
  }

  @Test
  void getAccount_whenUserNotFound_thenThrowsException() {
    String username = "nonExistentUser";

    UUID accountId = UUID.randomUUID();

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> dbAccountService.getAccount(username, accountId));
  }

  @Test
  void getAccount_whenAccountNotFound_thenThrowException() {
    String username = "testUser";

    User user = new User();
    user.setUsername(username);
    user.setId(UUID.randomUUID());

    UUID accountId = UUID.randomUUID();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class, () -> dbAccountService.getAccount(username, accountId));
  }

  @Test
  void getAccount_whenAccountExists_thenReturnAccountResponse() {
    String username = "testUser";
    UUID accountId = UUID.randomUUID();
    User user = new User();
    user.setUsername(username);
    user.setId(UUID.randomUUID());

    Account account = new Account();
    account.setId(accountId);
    account.setNumber("12345");
    account.setName("Test Account");

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(account));

    AccountResponse expectedResponse = AccountResponse.fromAccount(account);

    AccountResponse response = dbAccountService.getAccount(username, accountId);

    assertEquals(expectedResponse, response);
  }
}