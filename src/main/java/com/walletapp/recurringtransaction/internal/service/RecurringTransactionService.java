package com.walletapp.recurringtransaction.internal.service;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.account.internal.repository.AccountRepository;
import com.walletapp.category.internal.domain.Category;
import com.walletapp.category.internal.repository.CategoryRepository;
import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.currency.internal.repository.CurrencyRepository;
import com.walletapp.recurringtransaction.internal.domain.RecurringTransaction;
import com.walletapp.recurringtransaction.internal.mapper.RecurringTransactionMapper;
import com.walletapp.recurringtransaction.internal.repository.RecurringTransactionRepository;
import com.walletapp.recurringtransaction.web.request.CreateRecurringTransactionRequest;
import com.walletapp.recurringtransaction.web.request.UpdateRecurringTransactionRequest;
import com.walletapp.recurringtransaction.web.response.RecurringTransactionResponse;
import com.walletapp.shared.exception.ResourceNotFoundException;
import com.walletapp.user.internal.domain.User;
import com.walletapp.user.internal.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecurringTransactionService {

  private final RecurringTransactionRepository recurringTransactionRepository;
  private final AccountRepository accountRepository;
  private final CategoryRepository categoryRepository;
  private final CurrencyRepository currencyRepository;
  private final UserRepository userRepository;
  private final RecurringTransactionMapper recurringTransactionMapper;

  public List<RecurringTransactionResponse> findAllByUser(Long userId) {
    return recurringTransactionRepository.findAllByUserId(userId).stream()
        .map(recurringTransactionMapper::toResponse)
        .toList();
  }

  public RecurringTransactionResponse findById(Long id, Long userId) {
    return recurringTransactionMapper.toResponse(getOrThrow(id, userId));
  }

  public RecurringTransactionResponse create(
      CreateRecurringTransactionRequest request, Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

    Currency originalCurrency =
        currencyRepository
            .findById(request.originalCurrencyId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Currency not found: " + request.originalCurrencyId()));

    Account account =
        accountRepository
            .findByIdAndUserId(request.accountId(), userId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Account not found: " + request.accountId()));

    Category category = null;
    if (request.categoryId() != null) {
      category =
          categoryRepository
              .findByIdAndUserId(request.categoryId(), userId)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException("Category not found: " + request.categoryId()));
    }

    RecurringTransaction rt =
        RecurringTransaction.create(
            request.type(),
            request.originalAmount(),
            originalCurrency,
            request.frequency(),
            request.nextExecutionDate(),
            request.endDate(),
            request.description(),
            account,
            category,
            user);

    return recurringTransactionMapper.toResponse(recurringTransactionRepository.save(rt));
  }

  public RecurringTransactionResponse update(
      Long id, Long userId, UpdateRecurringTransactionRequest request) {
    RecurringTransaction rt = getOrThrow(id, userId);

    if (request.type() != null) rt.setType(request.type());
    if (request.originalAmount() != null) rt.setOriginalAmount(request.originalAmount());
    if (request.originalCurrencyId() != null) {
      Currency currency =
          currencyRepository
              .findById(request.originalCurrencyId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Currency not found: " + request.originalCurrencyId()));
      rt.setOriginalCurrency(currency);
    }
    if (request.frequency() != null) rt.setFrequency(request.frequency());
    if (request.nextExecutionDate() != null) rt.setNextExecutionDate(request.nextExecutionDate());
    if (request.endDate() != null) rt.setEndDate(request.endDate());
    if (request.description() != null) rt.setDescription(request.description());
    if (request.active() != null) rt.setActive(request.active());
    if (request.accountId() != null) {
      Account account =
          accountRepository
              .findByIdAndUserId(request.accountId(), userId)
              .orElseThrow(
                  () -> new ResourceNotFoundException("Account not found: " + request.accountId()));
      rt.setAccount(account);
    }
    if (request.categoryId() != null) {
      Category category =
          categoryRepository
              .findByIdAndUserId(request.categoryId(), userId)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException("Category not found: " + request.categoryId()));
      rt.setCategory(category);
    }

    return recurringTransactionMapper.toResponse(recurringTransactionRepository.save(rt));
  }

  public void delete(Long id, Long userId) {
    RecurringTransaction rt = getOrThrow(id, userId);
    recurringTransactionRepository.delete(rt);
  }

  private RecurringTransaction getOrThrow(Long id, Long userId) {
    return recurringTransactionRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Recurring transaction not found: " + id));
  }
}
