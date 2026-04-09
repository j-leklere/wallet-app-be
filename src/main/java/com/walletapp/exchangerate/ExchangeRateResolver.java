package com.walletapp.exchangerate;

import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.currency.internal.repository.CurrencyRepository;
import com.walletapp.shared.CurrencyCode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Central place for exchange-rate and reference-amount calculations. All services that need to
 * convert between ARS and USD should use this component instead of duplicating the logic.
 */
@Component
@RequiredArgsConstructor
public class ExchangeRateResolver {

  private final DolarApiService dolarApiService;
  private final CurrencyRepository currencyRepository;

  /**
   * Returns the exchange rate to store on a transaction. ARS → 1.0. USD → current USD/ARS rate for
   * the given date.
   */
  public BigDecimal resolveRate(Currency currency, LocalDate date) {
    return switch (currency.getCode()) {
      case CurrencyCode.USD -> dolarApiService.getUsdToArsRate(date);
      default -> BigDecimal.ONE;
    };
  }

  /**
   * Reference currency is always ARS. For ARS transactions it's the same currency; for USD it
   * fetches the ARS currency from the database.
   */
  public Currency resolveReferenceCurrency(Currency originalCurrency) {
    if (CurrencyCode.ARS.equals(originalCurrency.getCode())) return originalCurrency;
    return currencyRepository.findByCode(CurrencyCode.ARS).orElse(originalCurrency);
  }

  /**
   * Reference amount is the original amount expressed in ARS. ARS → same amount. USD →
   * originalAmount × exchangeRate.
   */
  public BigDecimal resolveReferenceAmount(
      Currency originalCurrency, BigDecimal originalAmount, BigDecimal exchangeRate) {
    if (CurrencyCode.ARS.equals(originalCurrency.getCode())) return originalAmount;
    return originalAmount.multiply(exchangeRate).setScale(4, RoundingMode.HALF_UP);
  }
}
