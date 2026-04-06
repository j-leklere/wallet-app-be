package com.walletapp.account.web;

import com.walletapp.account.internal.service.AccountService;
import com.walletapp.account.web.request.CreateAccountRequest;
import com.walletapp.account.web.request.UpdateAccountRequest;
import com.walletapp.account.web.response.AccountResponse;
import com.walletapp.auth.internal.service.AuthService;
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
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;
  private final AuthService authService;

  @GetMapping
  public ResponseEntity<List<AccountResponse>> findAll() {
    return ResponseEntity.ok(accountService.findAllByUser(authService.getCurrentUserId()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AccountResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(accountService.findById(id, authService.getCurrentUserId()));
  }

  @PostMapping
  public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(accountService.create(request, authService.getCurrentUserId()));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<AccountResponse> update(
      @PathVariable Long id, @Valid @RequestBody UpdateAccountRequest request) {
    return ResponseEntity.ok(accountService.update(id, authService.getCurrentUserId(), request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    accountService.delete(id, authService.getCurrentUserId());
    return ResponseEntity.noContent().build();
  }
}
