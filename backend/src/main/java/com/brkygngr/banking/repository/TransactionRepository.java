package com.brkygngr.banking.repository;

import com.brkygngr.banking.entity.Account;
import com.brkygngr.banking.entity.Transaction;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

  List<Transaction> findAllByFromOrTo(Account from, Account to);
}
