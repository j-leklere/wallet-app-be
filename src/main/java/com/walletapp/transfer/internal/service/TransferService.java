package com.walletapp.transfer.internal.service;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.account.internal.repository.AccountRepository;
import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.exchangerate.DolarApiService;
import com.walletapp.shared.PagedResponse;
import com.walletapp.shared.exception.ResourceNotFoundException;
import com.walletapp.transfer.internal.domain.Transfer;
import com.walletapp.transfer.internal.mapper.TransferMapper;
import com.walletapp.transfer.internal.repository.TransferRepository;
import com.walletapp.transfer.internal.specification.TransferSpecification;
import com.walletapp.transfer.web.request.CreateTransferRequest;
import com.walletapp.transfer.web.request.TransferFilter;
import com.walletapp.transfer.web.request.UpdateTransferRequest;
import com.walletapp.transfer.web.response.TransferResponse;
import com.walletapp.user.internal.domain.User;
import com.walletapp.user.internal.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferService {

  private final TransferRepository transferRepository;
  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final TransferMapper transferMapper;
  private final DolarApiService dolarApiService;

  public PagedResponse<TransferResponse> findAllByUser(
      Long userId, TransferFilter filter, Pageable pageable) {
    return PagedResponse.from(
        transferRepository
            .findAll(TransferSpecification.build(userId, filter), pageable)
            .map(transferMapper::toResponse));
  }

  public TransferResponse findById(Long id, Long userId) {
    return transferMapper.toResponse(getOrThrow(id, userId));
  }

  public TransferResponse create(CreateTransferRequest request, Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

    Account fromAccount =
        accountRepository
            .findByIdAndUserId(request.fromAccountId(), userId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Account not found: " + request.fromAccountId()));

    Account toAccount =
        accountRepository
            .findByIdAndUserId(request.toAccountId(), userId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Account not found: " + request.toAccountId()));

    Currency fromCurrency = fromAccount.getCurrency();
    Currency toCurrency = toAccount.getCurrency();

    BigDecimal exchangeRate = resolveExchangeRate(fromCurrency, toCurrency, request.date());
    BigDecimal toAmount =
        computeToAmount(request.fromAmount(), fromCurrency, toCurrency, exchangeRate);

    Transfer transfer =
        Transfer.create(
            request.fromAmount(),
            fromCurrency,
            toAmount,
            toCurrency,
            exchangeRate,
            request.date(),
            request.description(),
            fromAccount,
            toAccount,
            user);

    return transferMapper.toResponse(transferRepository.save(transfer));
  }

  public TransferResponse update(Long id, Long userId, UpdateTransferRequest request) {
    Transfer transfer = getOrThrow(id, userId);

    if (request.fromAmount() != null) transfer.setFromAmount(request.fromAmount());
    if (request.date() != null) transfer.setDate(request.date());
    if (request.description() != null) transfer.setDescription(request.description());
    if (request.fromAccountId() != null) {
      Account account =
          accountRepository
              .findByIdAndUserId(request.fromAccountId(), userId)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Account not found: " + request.fromAccountId()));
      transfer.setFromAccount(account);
      transfer.setFromCurrency(account.getCurrency());
    }
    if (request.toAccountId() != null) {
      Account account =
          accountRepository
              .findByIdAndUserId(request.toAccountId(), userId)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException("Account not found: " + request.toAccountId()));
      transfer.setToAccount(account);
      transfer.setToCurrency(account.getCurrency());
    }

    // Recompute amounts whenever anything relevant changes
    if (request.fromAmount() != null
        || request.fromAccountId() != null
        || request.toAccountId() != null
        || request.date() != null) {
      BigDecimal rate =
          resolveExchangeRate(
              transfer.getFromCurrency(), transfer.getToCurrency(), transfer.getDate());
      transfer.setExchangeRate(rate);
      transfer.setToAmount(
          computeToAmount(
              transfer.getFromAmount(),
              transfer.getFromCurrency(),
              transfer.getToCurrency(),
              rate));
    }

    return transferMapper.toResponse(transferRepository.save(transfer));
  }

  public void delete(Long id, Long userId) {
    Transfer transfer = getOrThrow(id, userId);
    transferRepository.delete(transfer);
  }

  private Transfer getOrThrow(Long id, Long userId) {
    return transferRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Transfer not found: " + id));
  }

  /**
   * Resolves the exchange rate between two currencies. Same currency → 1.0. ARS ↔ USD → fetch
   * USD/ARS rate from DolarAPI.
   */
  private BigDecimal resolveExchangeRate(Currency from, Currency to, java.time.LocalDate date) {
    if (from.getCode().equals(to.getCode())) return BigDecimal.ONE;
    BigDecimal usdToArs = dolarApiService.getUsdToArsRate(date);
    // from USD → ARS: rate = usdToArs
    // from ARS → USD: rate = 1/usdToArs (stored as how many ARS per unit of from-currency)
    if ("USD".equals(from.getCode())) return usdToArs;
    return BigDecimal.ONE.divide(usdToArs, 6, RoundingMode.HALF_UP);
  }

  private BigDecimal computeToAmount(
      BigDecimal fromAmount, Currency from, Currency to, BigDecimal exchangeRate) {
    if (from.getCode().equals(to.getCode())) return fromAmount;
    if ("USD".equals(from.getCode())) {
      // USD → ARS: toAmount = fromAmount * rate
      return fromAmount.multiply(exchangeRate).setScale(4, RoundingMode.HALF_UP);
    }
    // ARS → USD: toAmount = fromAmount * (1/usdToArs) = fromAmount * exchangeRate
    return fromAmount.multiply(exchangeRate).setScale(4, RoundingMode.HALF_UP);
  }
}
