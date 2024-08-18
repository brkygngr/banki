package com.brkygngr.banking.dto.transaction;

import com.brkygngr.banking.entity.Transaction;
import com.brkygngr.banking.entity.Transaction.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionHistoryResponse(UUID id,
                                         UUID from,
                                         UUID to,
                                         BigDecimal amount,
                                         LocalDateTime transactionDate,
                                         TransactionStatus status) {

  public static TransactionHistoryResponse fromTransaction(final Transaction transaction) {
    return new TransactionHistoryResponse(transaction.getId(),
                                          transaction.getFrom().getId(),
                                          transaction.getTo().getId(),
                                          transaction.getAmount(),
                                          transaction.getTransactionDate(),
                                          transaction.getStatus());
  }
}
