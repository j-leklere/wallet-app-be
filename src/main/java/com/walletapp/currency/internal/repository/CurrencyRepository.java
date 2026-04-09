package com.walletapp.currency.internal.repository;

import com.walletapp.currency.internal.domain.Currency;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

  Optional<Currency> findByCode(String code);

  List<Currency> findAllByActiveTrue();
}
