package com.walletapp.recurringtransaction.internal.repository;

import com.walletapp.recurringtransaction.internal.domain.RecurringTransaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

  List<RecurringTransaction> findAllByUserId(Long userId);

  Optional<RecurringTransaction> findByIdAndUserId(Long id, Long userId);

  List<RecurringTransaction> findTop5ByUserIdAndActiveTrueOrderByNextExecutionDateAsc(Long userId);

  /**
   * Finds all active recurring transactions due on or before the given date, where endDate is null
   * or not yet passed.
   */
  @Query(
      """
      SELECT r FROM RecurringTransaction r
      WHERE r.active = true
        AND r.nextExecutionDate <= :today
        AND (r.endDate IS NULL OR r.endDate >= r.nextExecutionDate)
      """)
  List<RecurringTransaction> findAllDue(@Param("today") LocalDate today);
}
