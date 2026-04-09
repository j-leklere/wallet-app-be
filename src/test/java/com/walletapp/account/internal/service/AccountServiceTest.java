package com.walletapp.account.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.account.internal.domain.AccountType;
import com.walletapp.account.internal.mapper.AccountMapper;
import com.walletapp.account.internal.repository.AccountRepository;
import com.walletapp.account.web.request.CreateAccountRequest;
import com.walletapp.account.web.request.UpdateAccountRequest;
import com.walletapp.account.web.response.AccountResponse;
import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.currency.internal.repository.CurrencyRepository;
import com.walletapp.shared.exception.ResourceNotFoundException;
import com.walletapp.transaction.internal.repository.TransactionRepository;
import com.walletapp.user.internal.domain.User;
import com.walletapp.user.internal.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock AccountRepository accountRepository;
  @Mock CurrencyRepository currencyRepository;
  @Mock UserRepository userRepository;
  @Mock AccountMapper accountMapper;
  @Mock TransactionRepository transactionRepository;

  @InjectMocks AccountService accountService;

  @Test
  void findAllByUser_returnsMappedList() {
    Account account = new Account();
    AccountResponse response =
        new AccountResponse(1L, "Checking", AccountType.CHECKING, 1L, "USD", "$", true, BigDecimal.ZERO);
    when(accountRepository.findAllByUserId(1L)).thenReturn(List.of(account));
    when(transactionRepository.findAllAccountBalances(1L)).thenReturn(List.of());
    when(accountMapper.toResponse(eq(account), any(BigDecimal.class))).thenReturn(response);

    List<AccountResponse> result = accountService.findAllByUser(1L);

    assertThat(result).containsExactly(response);
  }

  @Test
  void findById_returnsMappedAccount() {
    Account account = new Account();
    AccountResponse response =
        new AccountResponse(1L, "Checking", AccountType.CHECKING, 1L, "USD", "$", true, BigDecimal.ZERO);
    when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));
    when(transactionRepository.computeBalance(any())).thenReturn(BigDecimal.ZERO);
    when(accountMapper.toResponse(eq(account), eq(BigDecimal.ZERO))).thenReturn(response);

    AccountResponse result = accountService.findById(1L, 1L);

    assertThat(result).isEqualTo(response);
  }

  @Test
  void findById_throwsWhenNotFound() {
    when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> accountService.findById(1L, 1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("1");
  }

  @Test
  void create_savesAndReturnsAccount() {
    Currency currency = new Currency();
    User user = new User();
    Account saved = new Account();
    AccountResponse response =
        new AccountResponse(1L, "Checking", AccountType.CHECKING, 1L, "USD", "$", true, BigDecimal.ZERO);
    when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(accountRepository.save(any(Account.class))).thenReturn(saved);
    when(transactionRepository.computeBalance(any())).thenReturn(BigDecimal.ZERO);
    when(accountMapper.toResponse(eq(saved), eq(BigDecimal.ZERO))).thenReturn(response);

    AccountResponse result =
        accountService.create(new CreateAccountRequest("Checking", AccountType.CHECKING, 1L), 1L);

    assertThat(result).isEqualTo(response);
    verify(accountRepository).save(any(Account.class));
  }

  @Test
  void create_throwsWhenCurrencyNotFound() {
    when(currencyRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                accountService.create(
                    new CreateAccountRequest("Checking", AccountType.CHECKING, 1L), 1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("1");
  }

  @Test
  void create_throwsWhenUserNotFound() {
    when(currencyRepository.findById(1L)).thenReturn(Optional.of(new Currency()));
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                accountService.create(
                    new CreateAccountRequest("Checking", AccountType.CHECKING, 1L), 1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("1");
  }

  @Test
  void update_appliesChangesAndSaves() {
    Account account = new Account();
    Account saved = new Account();
    AccountResponse response =
        new AccountResponse(1L, "Updated", AccountType.SAVINGS, 1L, "USD", "$", false, BigDecimal.ZERO);
    when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));
    when(accountRepository.save(account)).thenReturn(saved);
    when(transactionRepository.computeBalance(any())).thenReturn(BigDecimal.ZERO);
    when(accountMapper.toResponse(eq(saved), eq(BigDecimal.ZERO))).thenReturn(response);

    AccountResponse result =
        accountService.update(
            1L, 1L, new UpdateAccountRequest("Updated", AccountType.SAVINGS, false));

    assertThat(result).isEqualTo(response);
  }

  @Test
  void delete_deletesAccount() {
    Account account = new Account();
    when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));
    when(transactionRepository.existsByAccountId(1L)).thenReturn(false);

    accountService.delete(1L, 1L);

    verify(accountRepository).delete(account);
  }

  @Test
  void delete_throwsWhenNotFound() {
    when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> accountService.delete(1L, 1L))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
