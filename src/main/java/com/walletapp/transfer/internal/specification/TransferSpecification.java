package com.walletapp.transfer.internal.specification;

import com.walletapp.transfer.internal.domain.Transfer;
import com.walletapp.transfer.web.request.TransferFilter;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class TransferSpecification {

  private TransferSpecification() {}

  public static Specification<Transfer> build(Long userId, TransferFilter filter) {
    return hasUser(userId)
        .and(dateFrom(filter.dateFrom()))
        .and(dateTo(filter.dateTo()))
        .and(hasFromAccount(filter.fromAccountId()))
        .and(hasToAccount(filter.toAccountId()));
  }

  private static Specification<Transfer> hasUser(Long userId) {
    return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
  }

  private static Specification<Transfer> dateFrom(LocalDate from) {
    return (root, query, cb) ->
        from == null ? null : cb.greaterThanOrEqualTo(root.get("date"), from);
  }

  private static Specification<Transfer> dateTo(LocalDate to) {
    return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("date"), to);
  }

  private static Specification<Transfer> hasFromAccount(Long accountId) {
    return (root, query, cb) ->
        accountId == null ? null : cb.equal(root.get("fromAccount").get("id"), accountId);
  }

  private static Specification<Transfer> hasToAccount(Long accountId) {
    return (root, query, cb) ->
        accountId == null ? null : cb.equal(root.get("toAccount").get("id"), accountId);
  }
}
