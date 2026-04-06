package com.walletapp.transfer.internal.service;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.account.internal.repository.AccountRepository;
import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.currency.internal.repository.CurrencyRepository;
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
  private final CurrencyRepository currencyRepository;
  private final UserRepository userRepository;
  private final TransferMapper transferMapper;

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

    Currency fromCurrency =
        currencyRepository
            .findById(request.fromCurrencyId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Currency not found: " + request.fromCurrencyId()));

    Currency toCurrency =
        currencyRepository
            .findById(request.toCurrencyId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Currency not found: " + request.toCurrencyId()));

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

    Transfer transfer =
        Transfer.create(
            request.fromAmount(),
            fromCurrency,
            request.toAmount(),
            toCurrency,
            request.exchangeRate(),
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
    if (request.fromCurrencyId() != null) {
      Currency currency =
          currencyRepository
              .findById(request.fromCurrencyId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Currency not found: " + request.fromCurrencyId()));
      transfer.setFromCurrency(currency);
    }
    if (request.toAmount() != null) transfer.setToAmount(request.toAmount());
    if (request.toCurrencyId() != null) {
      Currency currency =
          currencyRepository
              .findById(request.toCurrencyId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Currency not found: " + request.toCurrencyId()));
      transfer.setToCurrency(currency);
    }
    if (request.exchangeRate() != null) transfer.setExchangeRate(request.exchangeRate());
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
    }
    if (request.toAccountId() != null) {
      Account account =
          accountRepository
              .findByIdAndUserId(request.toAccountId(), userId)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException("Account not found: " + request.toAccountId()));
      transfer.setToAccount(account);
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
}
