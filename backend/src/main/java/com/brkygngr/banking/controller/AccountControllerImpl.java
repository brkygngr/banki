package com.brkygngr.banking.controller;

import com.brkygngr.banking.dto.account.AccountResponse;
import com.brkygngr.banking.dto.account.CreateAccountRequest;
import com.brkygngr.banking.dto.account.CreateAccountResponse;
import com.brkygngr.banking.dto.account.SearchAccountsQuery;
import com.brkygngr.banking.dto.account.UpdateAccountRequest;
import com.brkygngr.banking.service.AccountService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class AccountControllerImpl implements AccountController {

  private final AccountService accountService;

  @PostMapping
  @Override
  public ResponseEntity<CreateAccountResponse> createAccount(@RequestBody
                                                             @Valid final CreateAccountRequest createAccountRequest,
                                                             final JwtAuthenticationToken authentication) {
    Jwt principal = (Jwt) authentication.getPrincipal();

    String username = principal.getClaim("preferred_username");

    CreateAccountResponse response = accountService.createAccount(username, createAccountRequest);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                              .path("/{id}")
                                              .buildAndExpand(response.accountId())
                                              .toUri();

    return ResponseEntity.created(location).body(response);
  }

  @GetMapping
  @Override
  public ResponseEntity<Page<AccountResponse>> searchAccounts(@Valid final SearchAccountsQuery searchAccountsQuery,
                                                              final Pageable pageable,
                                                              final JwtAuthenticationToken authentication) {
    Jwt principal = (Jwt) authentication.getPrincipal();

    String username = principal.getClaim("preferred_username");

    SearchAccountsQuery query = searchAccountsQuery == null ? SearchAccountsQuery.empty() : searchAccountsQuery;

    Page<AccountResponse> response = accountService.searchAccounts(username, query, pageable);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{accountId}")
  @Override
  public ResponseEntity<Void> updateAccount(@PathVariable final UUID accountId,
                                            @RequestBody @Valid final UpdateAccountRequest updateAccountRequest,
                                            final JwtAuthenticationToken authentication) {
    Jwt principal = (Jwt) authentication.getPrincipal();

    String username = principal.getClaim("preferred_username");

    accountService.updateAccount(username, accountId, updateAccountRequest);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{accountId}")
  @Override
  public ResponseEntity<Void> deleteAccount(@PathVariable final UUID accountId,
                                            final JwtAuthenticationToken authentication) {
    Jwt principal = (Jwt) authentication.getPrincipal();

    String username = principal.getClaim("preferred_username");

    accountService.deleteAccount(username, accountId);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{accountId}")
  @Override
  public ResponseEntity<AccountResponse> getAccount(@PathVariable final UUID accountId,
                                                    final JwtAuthenticationToken authentication) {
    Jwt principal = (Jwt) authentication.getPrincipal();

    String username = principal.getClaim("preferred_username");

    AccountResponse response = accountService.getAccount(username, accountId);

    return ResponseEntity.ok(response);
  }
}