package com.walletapp.recurringtransaction.internal.repository;

import com.walletapp.recurringtransaction.internal.domain.RecurringTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

  List<RecurringTransaction> findAllByUserId(Long userId);

  Optional<RecurringTransaction> findByIdAndUserId(Long id, Long userId);
}
