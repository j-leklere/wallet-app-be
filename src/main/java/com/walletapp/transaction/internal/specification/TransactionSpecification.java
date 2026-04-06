package com.walletapp.transaction.internal.specification;

import com.walletapp.shared.TransactionType;
import com.walletapp.transaction.internal.domain.Transaction;
import com.walletapp.transaction.web.request.TransactionFilter;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecification {

  private TransactionSpecification() {}

  public static Specification<Transaction> build(Long userId, TransactionFilter filter) {
    return hasUser(userId)
        .and(hasType(filter.type()))
        .and(dateFrom(filter.dateFrom()))
        .and(dateTo(filter.dateTo()))
        .and(hasAccount(filter.accountId()))
        .and(hasCategory(filter.categoryId()));
  }

  private static Specification<Transaction> hasUser(Long userId) {
    return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
  }

  private static Specification<Transaction> hasType(TransactionType type) {
    return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
  }

  private static Specification<Transaction> dateFrom(LocalDate from) {
    return (root, query, cb) ->
        from == null ? null : cb.greaterThanOrEqualTo(root.get("date"), from);
  }

  private static Specification<Transaction> dateTo(LocalDate to) {
    return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("date"), to);
  }

  private static Specification<Transaction> hasAccount(Long accountId) {
    return (root, query, cb) ->
        accountId == null ? null : cb.equal(root.get("account").get("id"), accountId);
  }

  private static Specification<Transaction> hasCategory(Long categoryId) {
    return (root, query, cb) ->
        categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
  }
}
