package com.walletapp.transaction.internal.service;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.account.internal.repository.AccountRepository;
import com.walletapp.category.internal.domain.Category;
import com.walletapp.category.internal.repository.CategoryRepository;
import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.currency.internal.repository.CurrencyRepository;
import com.walletapp.exchangerate.DolarApiService;
import com.walletapp.shared.PagedResponse;
import com.walletapp.shared.exception.ResourceNotFoundException;
import com.walletapp.transaction.internal.domain.Transaction;
import com.walletapp.transaction.internal.mapper.TransactionMapper;
import com.walletapp.transaction.internal.repository.TransactionRepository;
import com.walletapp.transaction.internal.specification.TransactionSpecification;
import com.walletapp.transaction.web.request.CreateTransactionRequest;
import com.walletapp.transaction.web.request.TransactionFilter;
import com.walletapp.transaction.web.request.UpdateTransactionRequest;
import com.walletapp.transaction.web.response.TransactionResponse;
import com.walletapp.user.internal.domain.User;
import com.walletapp.user.internal.repository.UserRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final CategoryRepository categoryRepository;
  private final CurrencyRepository currencyRepository;
  private final UserRepository userRepository;
  private final TransactionMapper transactionMapper;
  private final DolarApiService dolarApiService;

  public PagedResponse<TransactionResponse> findAllByUser(
      Long userId, TransactionFilter filter, Pageable pageable) {
    return PagedResponse.from(
        transactionRepository
            .findAll(TransactionSpecification.build(userId, filter), pageable)
            .map(transactionMapper::toResponse));
  }

  public TransactionResponse findById(Long id, Long userId) {
    return transactionMapper.toResponse(getOrThrow(id, userId));
  }

  public TransactionResponse create(CreateTransactionRequest request, Long userId) {
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

    BigDecimal exchangeRate = resolveExchangeRate(originalCurrency, request.date());

    Transaction transaction =
        Transaction.create(
            request.type(),
            request.originalAmount(),
            originalCurrency,
            request.originalAmount(),
            originalCurrency,
            exchangeRate,
            request.date(),
            request.description(),
            account,
            category,
            user);

    return transactionMapper.toResponse(transactionRepository.save(transaction));
  }

  public TransactionResponse update(Long id, Long userId, UpdateTransactionRequest request) {
    Transaction transaction = getOrThrow(id, userId);

    if (request.type() != null) transaction.setType(request.type());
    if (request.date() != null) transaction.setDate(request.date());
    if (request.description() != null) transaction.setDescription(request.description());

    if (request.originalCurrencyId() != null) {
      Currency currency =
          currencyRepository
              .findById(request.originalCurrencyId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Currency not found: " + request.originalCurrencyId()));
      transaction.setOriginalCurrency(currency);
      transaction.setReferenceCurrency(currency);
    }
    if (request.originalAmount() != null) {
      transaction.setOriginalAmount(request.originalAmount());
      transaction.setReferenceAmount(request.originalAmount());
    }
    if (request.accountId() != null) {
      Account account =
          accountRepository
              .findByIdAndUserId(request.accountId(), userId)
              .orElseThrow(
                  () -> new ResourceNotFoundException("Account not found: " + request.accountId()));
      transaction.setAccount(account);
    }
    if (request.categoryId() != null) {
      Category category =
          categoryRepository
              .findByIdAndUserId(request.categoryId(), userId)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException("Category not found: " + request.categoryId()));
      transaction.setCategory(category);
    }

    // Recompute exchange rate if any of amount/currency/date changed
    if (request.originalAmount() != null
        || request.originalCurrencyId() != null
        || request.date() != null) {
      transaction.setExchangeRate(
          resolveExchangeRate(transaction.getOriginalCurrency(), transaction.getDate()));
    }

    return transactionMapper.toResponse(transactionRepository.save(transaction));
  }

  public void delete(Long id, Long userId) {
    Transaction transaction = getOrThrow(id, userId);
    transactionRepository.delete(transaction);
  }

  private Transaction getOrThrow(Long id, Long userId) {
    return transactionRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
  }

  /**
   * Returns the exchange rate to store on the transaction. ARS → 1.0 (no conversion needed). USD →
   * USD/ARS rate fetched from DolarAPI for the given date.
   */
  private BigDecimal resolveExchangeRate(Currency currency, java.time.LocalDate date) {
    return switch (currency.getCode()) {
      case "USD" -> dolarApiService.getUsdToArsRate(date);
      default -> BigDecimal.ONE;
    };
  }
}
