package com.brkygngr.banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "transaction")
@Data
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "from_account_id", nullable = false)
  private Account from;

  @ManyToOne
  @JoinColumn(name = "to_account_id", nullable = false)
  private Account to;

  @Column(nullable = false, precision = 24, scale = 6)
  private BigDecimal amount;

  @Column(nullable = false)
  private LocalDateTime transactionDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionStatus status;

  public enum TransactionStatus {
    SUCCESS,
    FAILED
  }
}
