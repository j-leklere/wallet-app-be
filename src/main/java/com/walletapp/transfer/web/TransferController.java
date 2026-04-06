package com.walletapp.transfer.web;

import com.walletapp.auth.internal.service.AuthService;
import com.walletapp.shared.PagedResponse;
import com.walletapp.transfer.internal.service.TransferService;
import com.walletapp.transfer.web.request.CreateTransferRequest;
import com.walletapp.transfer.web.request.TransferFilter;
import com.walletapp.transfer.web.request.UpdateTransferRequest;
import com.walletapp.transfer.web.response.TransferResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

  private final TransferService transferService;
  private final AuthService authService;

  @GetMapping
  public ResponseEntity<PagedResponse<TransferResponse>> findAll(
      TransferFilter filter,
      @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(
        transferService.findAllByUser(authService.getCurrentUserId(), filter, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TransferResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(transferService.findById(id, authService.getCurrentUserId()));
  }

  @PostMapping
  public ResponseEntity<TransferResponse> create(
      @Valid @RequestBody CreateTransferRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(transferService.create(request, authService.getCurrentUserId()));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<TransferResponse> update(
      @PathVariable Long id, @Valid @RequestBody UpdateTransferRequest request) {
    return ResponseEntity.ok(transferService.update(id, authService.getCurrentUserId(), request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    transferService.delete(id, authService.getCurrentUserId());
    return ResponseEntity.noContent().build();
  }
}
