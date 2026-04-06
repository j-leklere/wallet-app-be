package com.walletapp.transfer.internal.domain;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.shared.BaseEntity;
import com.walletapp.user.internal.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "transfer")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Transfer extends BaseEntity {

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal fromAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_currency_id", nullable = false)
  private Currency fromCurrency;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal toAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_currency_id", nullable = false)
  private Currency toCurrency;

  @Column(nullable = false, precision = 19, scale = 6)
  private BigDecimal exchangeRate;

  @Column(nullable = false)
  private LocalDate date;

  @Column(length = 255)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_account_id", nullable = false)
  private Account fromAccount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_account_id", nullable = false)
  private Account toAccount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public static Transfer create(
      BigDecimal fromAmount,
      Currency fromCurrency,
      BigDecimal toAmount,
      Currency toCurrency,
      BigDecimal exchangeRate,
      LocalDate date,
      String description,
      Account fromAccount,
      Account toAccount,
      User user) {
    Transfer transfer = new Transfer();
    transfer.fromAmount = fromAmount;
    transfer.fromCurrency = fromCurrency;
    transfer.toAmount = toAmount;
    transfer.toCurrency = toCurrency;
    transfer.exchangeRate = exchangeRate;
    transfer.date = date;
    transfer.description = description;
    transfer.fromAccount = fromAccount;
    transfer.toAccount = toAccount;
    transfer.user = user;
    return transfer;
  }
}
