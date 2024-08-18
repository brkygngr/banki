package com.brkygngr.banking.repository;

import com.brkygngr.banking.entity.Account;
import com.brkygngr.banking.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {

  Optional<Account> findByIdAndUser(UUID id, User user);

  void deleteByIdAndUser(UUID id, User user);

  boolean existsByIdAndUser(UUID id, User user);
}
