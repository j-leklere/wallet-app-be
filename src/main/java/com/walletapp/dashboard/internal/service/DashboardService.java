package com.walletapp.dashboard.internal.service;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.account.internal.repository.AccountRepository;
import com.walletapp.currency.internal.repository.CurrencyRepository;
import com.walletapp.dashboard.internal.projection.AccountBalanceEntry;
import com.walletapp.dashboard.internal.projection.CategoryExpenseEntry;
import com.walletapp.dashboard.internal.projection.MonthlyAmountEntry;
import com.walletapp.dashboard.internal.projection.PeriodSummaryEntry;
import com.walletapp.dashboard.web.response.DashboardAnalyticsResponse;
import com.walletapp.dashboard.web.response.DashboardAnalyticsResponse.CategoryExpense;
import com.walletapp.dashboard.web.response.DashboardAnalyticsResponse.ChartPoint;
import com.walletapp.dashboard.web.response.DashboardAnalyticsResponse.Summary;
import com.walletapp.dashboard.web.response.DashboardSnapshotResponse;
import com.walletapp.dashboard.web.response.DashboardSnapshotResponse.AccountEntry;
import com.walletapp.dashboard.web.response.DashboardSnapshotResponse.RecentTransaction;
import com.walletapp.dashboard.web.response.DashboardSnapshotResponse.UpcomingRecurring;
import com.walletapp.exchangerate.DolarApiService;
import com.walletapp.recurringtransaction.internal.domain.RecurringTransaction;
import com.walletapp.recurringtransaction.internal.repository.RecurringTransactionRepository;
import com.walletapp.shared.TransactionType;
import com.walletapp.transaction.internal.domain.Transaction;
import com.walletapp.transaction.internal.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final RecurringTransactionRepository recurringTransactionRepository;
  private final CurrencyRepository currencyRepository;
  private final DolarApiService dolarApiService;

  // ── Analytics ──────────────────────────────────────────────────────────────

  public DashboardAnalyticsResponse getAnalytics(
      Long userId, LocalDate dateFrom, LocalDate dateTo, Long currencyId, boolean consolidate) {

    Summary summary;
    List<ChartPoint> chart;
    List<CategoryExpense> categoryBreakdown;

    if (consolidate && currencyId != null) {
      ConsolidationParams params = buildConsolidationParams(currencyId);
      summary = buildSummaryConsolidated(userId, dateFrom, dateTo, params);
      chart = buildChartConsolidated(userId, params);
      categoryBreakdown = buildCategoryBreakdownConsolidated(userId, dateFrom, dateTo, params);
    } else {
      summary = buildSummary(userId, dateFrom, dateTo, currencyId);
      chart = buildChart(userId, currencyId);
      categoryBreakdown = buildCategoryBreakdown(userId, dateFrom, dateTo, currencyId);
    }

    return new DashboardAnalyticsResponse(summary, chart, categoryBreakdown);
  }

  private record ConsolidationParams(String targetCode, String otherCode, BigDecimal rate) {}

  private ConsolidationParams buildConsolidationParams(Long currencyId) {
    String targetCode = currencyRepository.findById(currencyId).orElseThrow().getCode();
    String otherCode = "ARS".equals(targetCode) ? "USD" : "ARS";
    BigDecimal usdToArs = dolarApiService.getUsdToArsRate();
    BigDecimal rate =
        "ARS".equals(targetCode)
            ? usdToArs
            : BigDecimal.ONE.divide(usdToArs, 10, RoundingMode.HALF_UP);
    return new ConsolidationParams(targetCode, otherCode, rate);
  }

  // ── Consolidated builders ──────────────────────────────────────────────────

  private Summary buildSummaryConsolidated(
      Long userId, LocalDate dateFrom, LocalDate dateTo, ConsolidationParams p) {
    PeriodSummaryEntry raw =
        transactionRepository.findPeriodSummaryConsolidated(
            userId, dateFrom, dateTo, p.targetCode(), p.otherCode(), p.rate());
    BigDecimal income = raw.income();
    BigDecimal expenses = raw.expenses();
    return new Summary(income, expenses, income.subtract(expenses));
  }

  private List<ChartPoint> buildChartConsolidated(Long userId, ConsolidationParams p) {
    LocalDate chartFrom = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(5);
    List<MonthlyAmountEntry> rawEntries =
        transactionRepository.findMonthlyAmountsConsolidated(
            userId, chartFrom, p.targetCode(), p.otherCode(), p.rate());
    return buildChartPoints(chartFrom, rawEntries);
  }

  private List<CategoryExpense> buildCategoryBreakdownConsolidated(
      Long userId, LocalDate dateFrom, LocalDate dateTo, ConsolidationParams p) {
    List<CategoryExpenseEntry> raw =
        transactionRepository.findCategoryExpensesConsolidated(
            userId, dateFrom, dateTo, p.targetCode(), p.otherCode(), p.rate());
    return buildCategoryExpenses(raw);
  }

  // ── Filter builders ────────────────────────────────────────────────────────

  private Summary buildSummary(Long userId, LocalDate dateFrom, LocalDate dateTo, Long currencyId) {
    PeriodSummaryEntry raw =
        transactionRepository.findPeriodSummary(userId, dateFrom, dateTo, currencyId);
    BigDecimal income = raw.income();
    BigDecimal expenses = raw.expenses();
    return new Summary(income, expenses, income.subtract(expenses));
  }

  private List<ChartPoint> buildChart(Long userId, Long currencyId) {
    LocalDate chartFrom = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(5);
    return buildChartPoints(
        chartFrom, transactionRepository.findMonthlyAmounts(userId, chartFrom, currencyId));
  }

  private List<ChartPoint> buildChartPoints(
      LocalDate chartFrom, List<MonthlyAmountEntry> rawEntries) {
    Map<Integer, Map<TransactionType, BigDecimal>> monthMap = new HashMap<>();
    for (MonthlyAmountEntry e : rawEntries) {
      int key = e.year() * 100 + e.month();
      monthMap
          .computeIfAbsent(key, k -> new EnumMap<>(TransactionType.class))
          .put(e.type(), e.total());
    }
    List<ChartPoint> chart = new ArrayList<>(6);
    for (int i = 0; i < 6; i++) {
      LocalDate month = chartFrom.plusMonths(i);
      int key = month.getYear() * 100 + month.getMonthValue();
      Map<TransactionType, BigDecimal> totals = monthMap.getOrDefault(key, Map.of());
      chart.add(
          new ChartPoint(
              month.getYear(),
              month.getMonthValue(),
              totals.getOrDefault(TransactionType.INCOME, BigDecimal.ZERO),
              totals.getOrDefault(TransactionType.EXPENSE, BigDecimal.ZERO)));
    }
    return chart;
  }

  private List<CategoryExpense> buildCategoryBreakdown(
      Long userId, LocalDate dateFrom, LocalDate dateTo, Long currencyId) {
    return buildCategoryExpenses(
        transactionRepository.findCategoryExpenses(userId, dateFrom, dateTo, currencyId));
  }

  private List<CategoryExpense> buildCategoryExpenses(List<CategoryExpenseEntry> raw) {
    BigDecimal totalExpenses =
        raw.stream().map(CategoryExpenseEntry::total).reduce(BigDecimal.ZERO, BigDecimal::add);
    if (totalExpenses.compareTo(BigDecimal.ZERO) == 0) return List.of();
    return raw.stream()
        .limit(6)
        .map(
            e ->
                new CategoryExpense(
                    e.categoryName(),
                    e.total(),
                    e.total()
                        .divide(totalExpenses, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(0, RoundingMode.HALF_UP)))
        .toList();
  }

  // ── Snapshot ───────────────────────────────────────────────────────────────

  public DashboardSnapshotResponse getSnapshot(Long userId) {
    List<AccountEntry> accounts = buildAccounts(userId);
    BigDecimal totalBalance =
        accounts.stream().map(AccountEntry::balance).reduce(BigDecimal.ZERO, BigDecimal::add);
    List<RecentTransaction> recentTransactions = buildRecentTransactions(userId);
    List<UpcomingRecurring> upcomingRecurring = buildUpcomingRecurring(userId);

    return new DashboardSnapshotResponse(
        totalBalance, accounts, recentTransactions, upcomingRecurring);
  }

  private List<AccountEntry> buildAccounts(Long userId) {
    // Un único query para todos los balances
    Map<Long, BigDecimal> balanceMap = new HashMap<>();
    for (AccountBalanceEntry e : transactionRepository.findAllAccountBalances(userId)) {
      balanceMap.put(e.accountId(), e.balance());
    }

    return accountRepository.findAllByUserId(userId).stream()
        .filter(Account::isActive)
        .map(
            a ->
                new AccountEntry(
                    a.getId(),
                    a.getName(),
                    a.getType(),
                    a.getCurrency().getCode(),
                    a.getCurrency().getSymbol(),
                    balanceMap.getOrDefault(a.getId(), BigDecimal.ZERO)))
        .toList();
  }

  private List<RecentTransaction> buildRecentTransactions(Long userId) {
    return transactionRepository.findTop5ByUserIdOrderByDateDescCreatedAtDesc(userId).stream()
        .map(DashboardService::toRecentTransaction)
        .toList();
  }

  private static RecentTransaction toRecentTransaction(Transaction t) {
    return new RecentTransaction(
        t.getId(),
        t.getDate(),
        t.getDescription(),
        t.getAccount().getName(),
        t.getCategory() != null ? t.getCategory().getName() : null,
        t.getOriginalAmount(),
        t.getOriginalCurrency().getCode(),
        t.getOriginalCurrency().getSymbol(),
        t.getType());
  }

  private List<UpcomingRecurring> buildUpcomingRecurring(Long userId) {
    return recurringTransactionRepository
        .findTop5ByUserIdAndActiveTrueOrderByNextExecutionDateAsc(userId)
        .stream()
        .map(DashboardService::toUpcomingRecurring)
        .toList();
  }

  private static UpcomingRecurring toUpcomingRecurring(RecurringTransaction r) {
    return new UpcomingRecurring(
        r.getId(),
        r.getDescription(),
        r.getOriginalAmount(),
        r.getOriginalCurrency().getCode(),
        r.getOriginalCurrency().getSymbol(),
        r.getNextExecutionDate(),
        r.getType());
  }
}
