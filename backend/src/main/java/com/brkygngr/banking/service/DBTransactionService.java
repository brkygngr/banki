package com.brkygngr.banking.service;

import com.brkygngr.banking.dto.transaction.TransactionHistoryResponse;
import com.brkygngr.banking.dto.transaction.TransferMoneyRequest;
import com.brkygngr.banking.dto.transaction.TransferMoneyResponse;
import com.brkygngr.banking.entity.Account;
import com.brkygngr.banking.entity.Transaction;
import com.brkygngr.banking.entity.Transaction.TransactionStatus;
import com.brkygngr.banking.entity.User;
import com.brkygngr.banking.exception.AccountNotFoundException;
import com.brkygngr.banking.exception.UserNotFoundException;
import com.brkygngr.banking.repository.AccountRepository;
import com.brkygngr.banking.repository.TransactionRepository;
import com.brkygngr.banking.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DBTransactionService implements TransactionService {

  private final TransactionRepository transactionRepository;

  private final UserRepository userRepository;

  private final AccountRepository accountRepository;

  private final MessageSource messageSource;

  @Transactional
  @Override
  public TransferMoneyResponse transferMoney(final String username, final TransferMoneyRequest transferMoneyRequest) {
    final User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::withDefaultMessage);

    log.info("User#{} is transferring {} from {} to {}",
             user.getId(),
             transferMoneyRequest.amount(),
             transferMoneyRequest.from(),
             transferMoneyRequest.to());

    final List<Account> accountList = accountRepository.findAllByIdInAndUser(List.of(transferMoneyRequest.from(),
                                                                                     transferMoneyRequest.to()), user);

    if (accountList.size() != 2) {
      log.warn("User#{} accounts {} {} not found!",
               user.getId(),
               transferMoneyRequest.from(),
               transferMoneyRequest.to());

      throw AccountNotFoundException.withDefaultMessage();
    }

    final Account from = accountList.stream()
                                    .filter(account -> account.getId().equals(transferMoneyRequest.from()))
                                    .toList()
                                    .getFirst();

    final Account to = accountList.stream()
                                  .filter(account -> account.getId().equals(transferMoneyRequest.to()))
                                  .toList()
                                  .getFirst();

    final Transaction transaction = new Transaction();
    transaction.setFrom(from);
    transaction.setTo(to);
    transaction.setTransactionDate(LocalDateTime.now());
    transaction.setAmount(transferMoneyRequest.amount());

    if (from.getBalance().compareTo(transaction.getAmount()) < 0) { //
      log.warn("User#{} account {} can not transfer {} amount!",
               user.getId(),
               from.getId(),
               transaction.getAmount());

      transaction.setStatus(TransactionStatus.FAILED);

      transactionRepository.save(transaction);

      return new TransferMoneyResponse(transaction.getStatus(),
                                       messageSource.getMessage("app.transaction.not.enough.money",
                                                                null,
                                                                Locale.ENGLISH));
    }

    transferMoney(transaction.getFrom(), transaction.getTo(), transaction.getAmount());

    transaction.setStatus(TransactionStatus.SUCCESS);

    transactionRepository.save(transaction);

    log.info("User#{} transferred {} from {} to {}",
             user.getId(),
             transaction.getAmount(),
             transaction.getFrom(),
             transaction.getTo());

    return new TransferMoneyResponse(transaction.getStatus(), "");
  }

  @Override
  public List<TransactionHistoryResponse> accountHistory(final String username, final UUID accountId) {
    User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::withDefaultMessage);

    Account account = accountRepository.findByIdAndUser(accountId, user)
                                       .orElseThrow(AccountNotFoundException::withDefaultMessage);

    return transactionRepository.findAllByFromOrTo(account, account)
                                .stream()
                                .map(TransactionHistoryResponse::fromTransaction)
                                .toList();
  }

  private synchronized void transferMoney(final Account from, final Account to, final BigDecimal amount) {
    from.setBalance(from.getBalance().subtract(amount));
    to.setBalance(to.getBalance().add(amount));

    accountRepository.saveAll(List.of(from, to));
  }
}
