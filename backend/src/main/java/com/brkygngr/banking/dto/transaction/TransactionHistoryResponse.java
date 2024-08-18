package com.brkygngr.banking.dto.transaction;

import com.brkygngr.banking.entity.Account;
import com.brkygngr.banking.entity.Transaction;
import com.brkygngr.banking.entity.Transaction.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionHistoryResponse(UUID id,
                                         Account from,
                                         Account to,
                                         BigDecimal amount,
                                         LocalDateTime transactionDate,
                                         TransactionStatus status) {

  public static TransactionHistoryResponse fromTransaction(Transaction transaction) {
    return new TransactionHistoryResponse(transaction.getId(),
                                          transaction.getFrom(),
                                          transaction.getTo(),
                                          transaction.getAmount(),
                                          transaction.getTransactionDate(),
                                          transaction.getStatus());
  }
}
