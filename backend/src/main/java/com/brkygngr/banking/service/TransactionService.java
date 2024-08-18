package com.brkygngr.banking.service;

import com.brkygngr.banking.dto.transaction.TransactionHistoryResponse;
import com.brkygngr.banking.dto.transaction.TransferMoneyRequest;
import com.brkygngr.banking.dto.transaction.TransferMoneyResponse;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

  public TransferMoneyResponse transferMoney(String username, TransferMoneyRequest transferMoneyRequest);

  public List<TransactionHistoryResponse> accountHistory(String username, UUID accountId);
}
