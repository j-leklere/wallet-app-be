package com.walletapp.transaction.internal.repository;

import com.walletapp.dashboard.internal.projection.AccountBalanceEntry;
import com.walletapp.dashboard.internal.projection.AccountDualBalanceEntry;
import com.walletapp.dashboard.internal.projection.CategoryExpenseEntry;
import com.walletapp.dashboard.internal.projection.MonthlyAmountEntry;
import com.walletapp.dashboard.internal.projection.PeriodSummaryEntry;
import com.walletapp.transaction.internal.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository
    extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

  List<Transaction> findAllByUserId(Long userId);

  boolean existsByAccountId(Long accountId);

  boolean existsByCategoryId(Long categoryId);

  Optional<Transaction> findByIdAndUserId(Long id, Long userId);

  List<Transaction> findTop5ByUserIdOrderByDateDescCreatedAtDesc(Long userId);

  // ── Dashboard queries ──────────────────────────────────────────────────────

  /** Balance acumulado por cuenta para un usuario (un único query en lugar de N). */
  @Query(
      "SELECT new com.walletapp.dashboard.internal.projection.AccountBalanceEntry("
          + "  t.account.id,"
          + "  SUM(CASE WHEN t.type = com.walletapp.shared.TransactionType.INCOME"
          + "      THEN t.referenceAmount ELSE -t.referenceAmount END)"
          + ") FROM Transaction t"
          + " WHERE t.user.id = :userId"
          + " GROUP BY t.account.id")
  List<AccountBalanceEntry> findAllAccountBalances(@Param("userId") Long userId);

  /** Balance nativo + balance en ARS por cuenta en un único query. */
  @Query(
      "SELECT new com.walletapp.dashboard.internal.projection.AccountDualBalanceEntry("
          + "  t.account.id,"
          + "  SUM(CASE WHEN t.type = com.walletapp.shared.TransactionType.INCOME"
          + "      THEN t.originalAmount ELSE -t.originalAmount END),"
          + "  SUM(CASE WHEN t.type = com.walletapp.shared.TransactionType.INCOME"
          + "      THEN t.referenceAmount ELSE -t.referenceAmount END)"
          + ") FROM Transaction t"
          + " WHERE t.user.id = :userId"
          + " GROUP BY t.account.id")
  List<AccountDualBalanceEntry> findAllAccountDualBalances(@Param("userId") Long userId);

  /** Balance de una sola cuenta (usado al crear/actualizar). */
  @Query(
      "SELECT COALESCE(SUM(CASE WHEN t.type = com.walletapp.shared.TransactionType.INCOME"
          + " THEN t.referenceAmount ELSE -t.referenceAmount END), 0)"
          + " FROM Transaction t WHERE t.account.id = :accountId")
  BigDecimal computeBalance(@Param("accountId") Long accountId);

  /** Suma de ingresos y gastos para un período, opcionalmente filtrada por moneda de referencia. */
  @Query(
      "SELECT new com.walletapp.dashboard.internal.projection.PeriodSummaryEntry("
          + "  COALESCE(SUM(CASE WHEN t.type = com.walletapp.shared.TransactionType.INCOME"
          + "      THEN t.referenceAmount ELSE 0 END), 0),"
          + "  COALESCE(SUM(CASE WHEN t.type = com.walletapp.shared.TransactionType.EXPENSE"
          + "      THEN t.referenceAmount ELSE 0 END), 0)"
          + ") FROM Transaction t"
          + " WHERE t.user.id = :userId AND t.date BETWEEN :dateFrom AND :dateTo"
          + "   AND (:currencyId IS NULL OR t.referenceCurrency.id = :currencyId)")
  PeriodSummaryEntry findPeriodSummary(
      @Param("userId") Long userId,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo,
      @Param("currencyId") Long currencyId);

  /** Totales mensuales por tipo desde una fecha dada (para el gráfico de 6 meses). */
  @Query(
      "SELECT new com.walletapp.dashboard.internal.projection.MonthlyAmountEntry("
          + "  year(t.date), month(t.date), t.type, SUM(t.referenceAmount)"
          + ") FROM Transaction t"
          + " WHERE t.user.id = :userId AND t.date >= :dateFrom"
          + "   AND (:currencyId IS NULL OR t.referenceCurrency.id = :currencyId)"
          + " GROUP BY year(t.date), month(t.date), t.type"
          + " ORDER BY year(t.date), month(t.date)")
  List<MonthlyAmountEntry> findMonthlyAmounts(
      @Param("userId") Long userId,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("currencyId") Long currencyId);

  // ── Consolidation queries (all transactions converted to a target currency) ──

  @Query(
      "SELECT new com.walletapp.dashboard.internal.projection.PeriodSummaryEntry("
          + "  COALESCE(SUM(CASE WHEN t.type = com.walletapp.shared.TransactionType.INCOME"
          + "      THEN CASE WHEN t.referenceCurrency.code = :targetCode THEN t.referenceAmount"
          + "               WHEN t.referenceCurrency.code = :otherCode  THEN t.referenceAmount * :rate"
          + "               ELSE 0 END ELSE 0 END), 0),"
          + "  COALESCE(SUM(CASE WHEN t.type = com.walletapp.shared.TransactionType.EXPENSE"
          + "      THEN CASE WHEN t.referenceCurrency.code = :targetCode THEN t.referenceAmount"
          + "               WHEN t.referenceCurrency.code = :otherCode  THEN t.referenceAmount * :rate"
          + "               ELSE 0 END ELSE 0 END), 0)"
          + ") FROM Transaction t"
          + " WHERE t.user.id = :userId AND t.date BETWEEN :dateFrom AND :dateTo")
  PeriodSummaryEntry findPeriodSummaryConsolidated(
      @Param("userId") Long userId,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo,
      @Param("targetCode") String targetCode,
      @Param("otherCode") String otherCode,
      @Param("rate") BigDecimal rate);

  @Query(
      "SELECT new com.walletapp.dashboard.internal.projection.MonthlyAmountEntry("
          + "  year(t.date), month(t.date), t.type,"
          + "  SUM(CASE WHEN t.referenceCurrency.code = :targetCode THEN t.referenceAmount"
          + "           WHEN t.referenceCurrency.code = :otherCode  THEN t.referenceAmount * :rate"
          + "           ELSE t.referenceAmount END)"
          + ") FROM Transaction t"
          + " WHERE t.user.id = :userId AND t.date >= :dateFrom"
          + " GROUP BY year(t.date), month(t.date), t.type"
          + " ORDER BY year(t.date), month(t.date)")
  List<MonthlyAmountEntry> findMonthlyAmountsConsolidated(
      @Param("userId") Long userId,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("targetCode") String targetCode,
      @Param("otherCode") String otherCode,
      @Param("rate") BigDecimal rate);

  @Query(
      "SELECT new com.walletapp.dashboard.internal.projection.CategoryExpenseEntry("
          + "  COALESCE(c.name, 'Sin categoría'),"
          + "  SUM(CASE WHEN t.referenceCurrency.code = :targetCode THEN t.referenceAmount"
          + "           WHEN t.referenceCurrency.code = :otherCode  THEN t.referenceAmount * :rate"
          + "           ELSE t.referenceAmount END),"
          + "  COALESCE(c.colorKey, 'slate')"
          + ") FROM Transaction t LEFT JOIN t.category c"
          + " WHERE t.user.id = :userId"
          + "   AND t.type = com.walletapp.shared.TransactionType.EXPENSE"
          + "   AND t.date BETWEEN :dateFrom AND :dateTo"
          + " GROUP BY c.id, c.name, c.colorKey"
          + " ORDER BY SUM(t.referenceAmount) DESC")
  List<CategoryExpenseEntry> findCategoryExpensesConsolidated(
      @Param("userId") Long userId,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo,
      @Param("targetCode") String targetCode,
      @Param("otherCode") String otherCode,
      @Param("rate") BigDecimal rate);

  /** Gastos agrupados por categoría para un período, ordenados de mayor a menor. */
  @Query(
      "SELECT new com.walletapp.dashboard.internal.projection.CategoryExpenseEntry("
          + "  COALESCE(c.name, 'Sin categoría'), SUM(t.referenceAmount), COALESCE(c.colorKey, 'slate')"
          + ") FROM Transaction t LEFT JOIN t.category c"
          + " WHERE t.user.id = :userId"
          + "   AND t.type = com.walletapp.shared.TransactionType.EXPENSE"
          + "   AND t.date BETWEEN :dateFrom AND :dateTo"
          + "   AND (:currencyId IS NULL OR t.referenceCurrency.id = :currencyId)"
          + " GROUP BY c.id, c.name, c.colorKey"
          + " ORDER BY SUM(t.referenceAmount) DESC")
  List<CategoryExpenseEntry> findCategoryExpenses(
      @Param("userId") Long userId,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo,
      @Param("currencyId") Long currencyId);
}
