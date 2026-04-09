package com.walletapp.account.internal.service;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.account.internal.mapper.AccountMapper;
import com.walletapp.account.internal.repository.AccountRepository;
import com.walletapp.account.web.request.CreateAccountRequest;
import com.walletapp.account.web.request.UpdateAccountRequest;
import com.walletapp.account.web.response.AccountResponse;
import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.currency.internal.repository.CurrencyRepository;
import com.walletapp.dashboard.internal.projection.AccountBalanceEntry;
import com.walletapp.shared.exception.EntityInUseException;
import com.walletapp.shared.exception.ResourceNotFoundException;
import com.walletapp.transaction.internal.repository.TransactionRepository;
import com.walletapp.user.internal.domain.User;
import com.walletapp.user.internal.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final CurrencyRepository currencyRepository;
  private final UserRepository userRepository;
  private final AccountMapper accountMapper;
  private final TransactionRepository transactionRepository;

  public List<AccountResponse> findAllByUser(Long userId) {
    Map<Long, BigDecimal> balanceMap =
        transactionRepository.findAllAccountBalances(userId).stream()
            .collect(
                Collectors.toMap(AccountBalanceEntry::accountId, AccountBalanceEntry::balance));

    return accountRepository.findAllByUserId(userId).stream()
        .map(
            account ->
                accountMapper.toResponse(
                    account, balanceMap.getOrDefault(account.getId(), BigDecimal.ZERO)))
        .toList();
  }

  public AccountResponse findById(Long id, Long userId) {
    Account account = getOrThrow(id, userId);
    return accountMapper.toResponse(account, transactionRepository.computeBalance(account.getId()));
  }

  public AccountResponse create(CreateAccountRequest request, Long userId) {
    Currency currency =
        currencyRepository
            .findById(request.currencyId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Currency not found: " + request.currencyId()));

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

    Account account = Account.create(request.name(), request.type(), currency, user);
    Account saved = accountRepository.save(account);
    return accountMapper.toResponse(saved, transactionRepository.computeBalance(saved.getId()));
  }

  public AccountResponse update(Long id, Long userId, UpdateAccountRequest request) {
    Account account = getOrThrow(id, userId);
    if (request.name() != null) account.setName(request.name());
    if (request.type() != null) account.setType(request.type());
    if (request.active() != null) account.setActive(request.active());
    Account saved = accountRepository.save(account);
    return accountMapper.toResponse(saved, transactionRepository.computeBalance(saved.getId()));
  }

  public void delete(Long id, Long userId) {
    Account account = getOrThrow(id, userId);
    if (transactionRepository.existsByAccountId(id)) {
      throw new EntityInUseException(
          "No se puede eliminar la cuenta porque tiene transacciones asociadas. Desactivala en su lugar.");
    }
    accountRepository.delete(account);
  }

  private Account getOrThrow(Long id, Long userId) {
    return accountRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
  }
}
