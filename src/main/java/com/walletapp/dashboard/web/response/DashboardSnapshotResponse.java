package com.walletapp.dashboard.web.response;

import com.walletapp.account.internal.domain.AccountType;
import com.walletapp.shared.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardSnapshotResponse(
    BigDecimal totalBalance,
    List<AccountEntry> accounts,
    List<RecentTransaction> recentTransactions,
    List<UpcomingRecurring> upcomingRecurring) {

  public record AccountEntry(
      Long id,
      String name,
      AccountType type,
      String currencyCode,
      String currencySymbol,
      BigDecimal balance,
      BigDecimal arsBalance) {}

  public record RecentTransaction(
      Long id,
      LocalDate date,
      String description,
      String accountName,
      String categoryName,
      BigDecimal amount,
      String currencyCode,
      String currencySymbol,
      TransactionType type) {}

  public record UpcomingRecurring(
      Long id,
      String description,
      BigDecimal amount,
      String currencyCode,
      String currencySymbol,
      LocalDate nextDate,
      TransactionType type) {}
}
