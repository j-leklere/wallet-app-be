package com.walletapp.currency.web;

import com.walletapp.currency.internal.service.CurrencyService;
import com.walletapp.currency.web.response.CurrencyResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
public class CurrencyController {

  private final CurrencyService currencyService;

  @GetMapping
  public ResponseEntity<List<CurrencyResponse>> findAll() {
    return ResponseEntity.ok(currencyService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CurrencyResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(currencyService.findById(id));
  }
}
