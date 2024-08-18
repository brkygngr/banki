package com.brkygngr.banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"user", "createdAt", "updatedAt"})
@EqualsAndHashCode(exclude = {"user", "balance", "createdAt", "updatedAt"})
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true, nullable = false)
  private String number;

  @Column(unique = true, nullable = false)
  private String name;

  @Column(nullable = false, precision = 24, scale = 6)
  private BigDecimal balance;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
