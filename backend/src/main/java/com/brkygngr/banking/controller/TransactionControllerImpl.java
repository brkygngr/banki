package com.brkygngr.banking.controller;

import com.brkygngr.banking.dto.ExceptionResponse;
import com.brkygngr.banking.dto.transaction.TransactionHistoryResponse;
import com.brkygngr.banking.dto.transaction.TransferMoneyRequest;
import com.brkygngr.banking.dto.transaction.TransferMoneyResponse;
import com.brkygngr.banking.entity.Transaction.TransactionStatus;
import com.brkygngr.banking.exception.ExceptionCode;
import com.brkygngr.banking.service.TransactionService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")
public class TransactionControllerImpl implements TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/transfer")
  @Override
  public ResponseEntity<?> transferMoney(@RequestBody @Valid final TransferMoneyRequest transferMoneyRequest,
                                         final JwtAuthenticationToken authentication) {
    Jwt principal = (Jwt) authentication.getPrincipal();

    String username = principal.getClaim("preferred_username");

    TransferMoneyResponse transferMoneyResponse = transactionService.transferMoney(username, transferMoneyRequest);

    if (transferMoneyResponse.status().equals(TransactionStatus.SUCCESS)) {
      return ResponseEntity.noContent().build();
    }

    ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(),
                                                                ExceptionCode.TRANSACTION_NOT_ENOUGH_MONEY,
                                                                new String[]{transferMoneyResponse.reason()});

    return ResponseEntity.unprocessableEntity().body(exceptionResponse);
  }

  @GetMapping("/account/{accountId}")
  @Override
  public ResponseEntity<List<TransactionHistoryResponse>> accountHistory(@PathVariable final UUID accountId,
                                                                         final JwtAuthenticationToken authentication) {
    Jwt principal = (Jwt) authentication.getPrincipal();

    String username = principal.getClaim("preferred_username");

    return ResponseEntity.ok(transactionService.accountHistory(username, accountId));
  }
}
