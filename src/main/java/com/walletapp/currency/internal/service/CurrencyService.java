package com.walletapp.currency.internal.service;

import com.walletapp.currency.internal.mapper.CurrencyMapper;
import com.walletapp.currency.internal.repository.CurrencyRepository;
import com.walletapp.currency.web.response.CurrencyResponse;
import com.walletapp.shared.exception.ResourceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CurrencyService {

  private final CurrencyRepository currencyRepository;
  private final CurrencyMapper currencyMapper;

  public List<CurrencyResponse> findAll() {
    return currencyRepository.findAllByActiveTrue().stream()
        .map(currencyMapper::toResponse)
        .toList();
  }

  public CurrencyResponse findById(Long id) {
    return currencyMapper.toResponse(
        currencyRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Currency not found: " + id)));
  }
}
