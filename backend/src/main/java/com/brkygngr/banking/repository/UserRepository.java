package com.brkygngr.banking.repository;

import com.brkygngr.banking.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByUsernameOrEmail(String username, String email);
}
