package com.walletapp.currency.internal.repository;

import com.walletapp.currency.internal.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {}
