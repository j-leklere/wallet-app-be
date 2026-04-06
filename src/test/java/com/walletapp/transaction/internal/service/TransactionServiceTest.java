package com.walletapp.transaction.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.account.internal.repository.AccountRepository;
import com.walletapp.category.internal.domain.Category;
import com.walletapp.category.internal.repository.CategoryRepository;
import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.currency.internal.repository.CurrencyRepository;
import com.walletapp.shared.PagedResponse;
import com.walletapp.shared.TransactionType;
import com.walletapp.shared.exception.ResourceNotFoundException;
import com.walletapp.transaction.internal.domain.Transaction;
import com.walletapp.transaction.internal.mapper.TransactionMapper;
import com.walletapp.transaction.internal.repository.TransactionRepository;
import com.walletapp.transaction.internal.specification.TransactionSpecification;
import com.walletapp.transaction.web.request.CreateTransactionRequest;
import com.walletapp.transaction.web.request.TransactionFilter;
import com.walletapp.transaction.web.response.TransactionResponse;
import com.walletapp.user.internal.domain.User;
import com.walletapp.user.internal.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @Mock TransactionRepository transactionRepository;
  @Mock AccountRepository accountRepository;
  @Mock CategoryRepository categoryRepository;
  @Mock CurrencyRepository currencyRepository;
  @Mock UserRepository userRepository;
  @Mock TransactionMapper transactionMapper;

  @InjectMocks TransactionService transactionService;

  @Test
  void findAllByUser_returnsPagedResponse() {
    Transaction tx = new Transaction();
    TransactionResponse response = buildResponse();
    Pageable pageable = PageRequest.of(0, 20);
    TransactionFilter filter = new TransactionFilter(null, null, null, null, null);

    try (MockedStatic<TransactionSpecification> specs =
        Mockito.mockStatic(TransactionSpecification.class)) {
      Specification<Transaction> spec = (root, query, cb) -> null;
      specs.when(() -> TransactionSpecification.build(1L, filter)).thenReturn(spec);
      when(transactionRepository.findAll(eq(spec), eq(pageable)))
          .thenReturn(new PageImpl<>(List.of(tx)));
      when(transactionMapper.toResponse(tx)).thenReturn(response);

      PagedResponse<TransactionResponse> result =
          transactionService.findAllByUser(1L, filter, pageable);

      assertThat(result.content()).containsExactly(response);
      assertThat(result.totalElements()).isEqualTo(1);
    }
  }

  @Test
  void findById_returnsMappedTransaction() {
    Transaction tx = new Transaction();
    TransactionResponse response = buildResponse();
    when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(tx));
    when(transactionMapper.toResponse(tx)).thenReturn(response);

    TransactionResponse result = transactionService.findById(1L, 1L);

    assertThat(result).isEqualTo(response);
  }

  @Test
  void findById_throwsWhenNotFound() {
    when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> transactionService.findById(1L, 1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("1");
  }

  @Test
  void create_savesAndReturnsTransaction() {
    User user = new User();
    Currency currency = new Currency();
    Account account = new Account();
    Transaction saved = new Transaction();
    TransactionResponse response = buildResponse();

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
    when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);
    when(transactionMapper.toResponse(saved)).thenReturn(response);

    CreateTransactionRequest request =
        new CreateTransactionRequest(
            TransactionType.EXPENSE,
            BigDecimal.TEN,
            1L,
            BigDecimal.TEN,
            1L,
            BigDecimal.ONE,
            LocalDate.now(),
            "Test",
            1L,
            null);

    TransactionResponse result = transactionService.create(request, 1L);

    assertThat(result).isEqualTo(response);
    verify(transactionRepository).save(any(Transaction.class));
  }

  @Test
  void create_withCategory_looksCategoryUp() {
    User user = new User();
    Currency currency = new Currency();
    Account account = new Account();
    Category category = new Category();
    Transaction saved = new Transaction();
    TransactionResponse response = buildResponse();

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
    when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));
    when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);
    when(transactionMapper.toResponse(saved)).thenReturn(response);

    CreateTransactionRequest request =
        new CreateTransactionRequest(
            TransactionType.EXPENSE,
            BigDecimal.TEN,
            1L,
            BigDecimal.TEN,
            1L,
            BigDecimal.ONE,
            LocalDate.now(),
            "Test",
            1L,
            1L);

    transactionService.create(request, 1L);

    verify(categoryRepository).findByIdAndUserId(1L, 1L);
  }

  @Test
  void create_throwsWhenAccountNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
    when(currencyRepository.findById(1L)).thenReturn(Optional.of(new Currency()));
    when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

    CreateTransactionRequest request =
        new CreateTransactionRequest(
            TransactionType.EXPENSE,
            BigDecimal.TEN,
            1L,
            BigDecimal.TEN,
            1L,
            BigDecimal.ONE,
            LocalDate.now(),
            null,
            1L,
            null);

    assertThatThrownBy(() -> transactionService.create(request, 1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("1");
  }

  @Test
  void delete_deletesTransaction() {
    Transaction tx = new Transaction();
    when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(tx));

    transactionService.delete(1L, 1L);

    verify(transactionRepository).delete(tx);
  }

  @Test
  void delete_throwsWhenNotFound() {
    when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> transactionService.delete(1L, 1L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  private TransactionResponse buildResponse() {
    return new TransactionResponse(
        1L,
        TransactionType.EXPENSE,
        BigDecimal.TEN,
        1L,
        "USD",
        "$",
        BigDecimal.TEN,
        1L,
        "USD",
        "$",
        BigDecimal.ONE,
        LocalDate.now(),
        "Test",
        1L,
        null);
  }
}
