package com.walletapp.recurringtransaction.internal.scheduler;

import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.currency.internal.repository.CurrencyRepository;
import com.walletapp.exchangerate.DolarApiService;
import com.walletapp.recurringtransaction.internal.domain.RecurringFrequency;
import com.walletapp.recurringtransaction.internal.domain.RecurringTransaction;
import com.walletapp.recurringtransaction.internal.repository.RecurringTransactionRepository;
import com.walletapp.transaction.internal.domain.Transaction;
import com.walletapp.transaction.internal.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecurringTransactionScheduler {

  private final RecurringTransactionRepository recurringRepository;
  private final TransactionRepository transactionRepository;
  private final CurrencyRepository currencyRepository;
  private final DolarApiService dolarApiService;

  /** Runs every day at 01:00 server time. */
  @Scheduled(cron = "0 0 1 * * *")
  @Transactional
  public void executeRecurring() {
    LocalDate today = LocalDate.now();
    List<RecurringTransaction> due = recurringRepository.findAllDue(today);

    log.info("Recurring scheduler: {} transactions due on {}", due.size(), today);

    for (RecurringTransaction rt : due) {
      try {
        createTransaction(rt);
        advanceNextExecutionDate(rt, today);
      } catch (Exception e) {
        log.error(
            "Failed to execute recurring transaction id={}: {}", rt.getId(), e.getMessage(), e);
      }
    }
  }

  private void createTransaction(RecurringTransaction rt) {
    Currency originalCurrency = rt.getOriginalCurrency();
    BigDecimal exchangeRate = resolveExchangeRate(originalCurrency, rt.getNextExecutionDate());
    Currency referenceCurrency = resolveReferenceCurrency(originalCurrency);
    BigDecimal referenceAmount = resolveReferenceAmount(originalCurrency, rt.getOriginalAmount(), exchangeRate);

    Transaction transaction =
        Transaction.create(
            rt.getType(),
            rt.getOriginalAmount(),
            originalCurrency,
            referenceAmount,
            referenceCurrency,
            exchangeRate,
            rt.getNextExecutionDate(),
            rt.getDescription(),
            rt.getAccount(),
            rt.getCategory(),
            rt.getUser());

    transactionRepository.save(transaction);
    log.debug(
        "Created transaction for recurring id={}, date={}", rt.getId(), rt.getNextExecutionDate());
  }

  private BigDecimal resolveExchangeRate(Currency currency, LocalDate date) {
    return switch (currency.getCode()) {
      case "USD" -> dolarApiService.getUsdToArsRate(date);
      default -> BigDecimal.ONE;
    };
  }

  private Currency resolveReferenceCurrency(Currency originalCurrency) {
    if ("ARS".equals(originalCurrency.getCode())) return originalCurrency;
    return currencyRepository.findByCode("ARS").orElse(originalCurrency);
  }

  private BigDecimal resolveReferenceAmount(
      Currency originalCurrency, BigDecimal originalAmount, BigDecimal exchangeRate) {
    if ("ARS".equals(originalCurrency.getCode())) return originalAmount;
    return originalAmount.multiply(exchangeRate).setScale(4, RoundingMode.HALF_UP);
  }

  private void advanceNextExecutionDate(RecurringTransaction rt, LocalDate today) {
    LocalDate next = computeNext(rt.getNextExecutionDate(), rt.getFrequency(), today);

    if (rt.getEndDate() != null && next.isAfter(rt.getEndDate())) {
      rt.setActive(false);
      log.debug(
          "Recurring id={} deactivated: next date {} exceeds end date {}",
          rt.getId(),
          next,
          rt.getEndDate());
    } else {
      rt.setNextExecutionDate(next);
    }
  }

  /**
   * Advances from {@code current} by one frequency step, ensuring the result is
   * strictly after {@code today} to handle catch-up on missed executions.
   */
  private LocalDate computeNext(LocalDate current, RecurringFrequency frequency, LocalDate today) {
    LocalDate next = advance(current, frequency);
    while (!next.isAfter(today)) {
      next = advance(next, frequency);
    }
    return next;
  }

  private LocalDate advance(LocalDate date, RecurringFrequency frequency) {
    return switch (frequency) {
      case DAILY -> date.plusDays(1);
      case WEEKLY -> date.plusWeeks(1);
      case MONTHLY -> date.plusMonths(1);
      case YEARLY -> date.plusYears(1);
    };
  }
}
