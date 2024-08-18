package com.brkygngr.banking.dto.transaction;

import com.brkygngr.banking.entity.Transaction.TransactionStatus;

public record TransferMoneyResponse(TransactionStatus status, String reason) {

}
