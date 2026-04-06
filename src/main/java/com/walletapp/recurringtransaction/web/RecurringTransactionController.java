package com.walletapp.recurringtransaction.web;

import com.walletapp.auth.internal.service.AuthService;
import com.walletapp.recurringtransaction.internal.service.RecurringTransactionService;
import com.walletapp.recurringtransaction.web.request.CreateRecurringTransactionRequest;
import com.walletapp.recurringtransaction.web.request.UpdateRecurringTransactionRequest;
import com.walletapp.recurringtransaction.web.response.RecurringTransactionResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recurring-transactions")
@RequiredArgsConstructor
public class RecurringTransactionController {

  private final RecurringTransactionService recurringTransactionService;
  private final AuthService authService;

  @GetMapping
  public ResponseEntity<List<RecurringTransactionResponse>> findAll() {
    return ResponseEntity.ok(
        recurringTransactionService.findAllByUser(authService.getCurrentUserId()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<RecurringTransactionResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(
        recurringTransactionService.findById(id, authService.getCurrentUserId()));
  }

  @PostMapping
  public ResponseEntity<RecurringTransactionResponse> create(
      @Valid @RequestBody CreateRecurringTransactionRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(recurringTransactionService.create(request, authService.getCurrentUserId()));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<RecurringTransactionResponse> update(
      @PathVariable Long id, @Valid @RequestBody UpdateRecurringTransactionRequest request) {
    return ResponseEntity.ok(
        recurringTransactionService.update(id, authService.getCurrentUserId(), request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    recurringTransactionService.delete(id, authService.getCurrentUserId());
    return ResponseEntity.noContent().build();
  }
}
